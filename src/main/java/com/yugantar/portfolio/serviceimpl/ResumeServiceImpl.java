package com.yugantar.portfolio.serviceimpl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yugantar.portfolio.dto.ResumeDownloadInfo;
import com.yugantar.portfolio.dto.ResumeResponse;
import com.yugantar.portfolio.dto.ResumeUploadResponse;
import com.yugantar.portfolio.entity.ResumeEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.ResumeRepository;
import com.yugantar.portfolio.service.ResumeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

	private final ResumeRepository resumeRepository;

	// Allowed file types
	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("application/pdf", "application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document");

	// Allowed file extensions
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");

	// Maximum file size: 10MB
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB in bytes

	@Override
	@Transactional
	public ResumeUploadResponse uploadResume(MultipartFile file) {
		log.info("Starting resume upload process for file: {}", file.getOriginalFilename());

		try {
			// Validate file
			String validationError = validateResumeFile(file);
			if (validationError != null) {
				log.warn("Resume upload validation failed: {}", validationError);
				return ResumeUploadResponse.builder().success(false).message(validationError)
						.errorCode("VALIDATION_ERROR").build();
			}

			// Generate unique file name
			String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
			String fileExtension = getFileExtension(originalFileName);
			String uniqueFileName = generateUniqueFileName(originalFileName, fileExtension);

			// Deactivate all existing resumes
			resumeRepository.deactivateAllResumes();
			log.info("Deactivated all existing resumes");

			// Create resume entity
			ResumeEntity resumeEntity = ResumeEntity.builder().fileName(uniqueFileName)
					.originalFileName(originalFileName).fileFormat(fileExtension.toUpperCase()).fileSize(file.getSize())
					.contentType(file.getContentType()).fileData(file.getBytes()).uploadedDate(LocalDateTime.now())
					.isActive(true).build();

			// Save to database
			ResumeEntity savedResume = resumeRepository.save(resumeEntity);
			log.info("Resume uploaded successfully with ID: {}, Size: {} bytes", savedResume.getId(),
					savedResume.getFileSize());

			// Create response
			ResumeResponse resumeResponse = mapToResponse(savedResume);

			return ResumeUploadResponse.builder().success(true).message("Resume uploaded successfully")
					.resumeInfo(resumeResponse).build();

		} catch (IOException e) {
			log.error("Error reading file data during resume upload", e);
			return ResumeUploadResponse.builder().success(false).message("Error reading file data. Please try again.")
					.errorCode("FILE_READ_ERROR").build();
		} catch (Exception e) {
			log.error("Unexpected error during resume upload", e);
			return ResumeUploadResponse.builder().success(false)
					.message("An unexpected error occurred during upload. Please try again.")
					.errorCode("INTERNAL_ERROR").build();
		}
	}

	@Override
	public ResumeEntity getActiveResume() {
		log.debug("Fetching active resume");
		return resumeRepository.findByIsActiveTrue().orElse(null);
	}

	@Override
	public ResumeDownloadInfo getResumeDownloadInfo() {
		log.debug("Getting resume download info");

		Optional<ResumeEntity> activeResume = resumeRepository.findByIsActiveTrue();

		if (activeResume.isPresent()) {
			ResumeEntity resume = activeResume.get();
			return ResumeDownloadInfo.builder().available(true).fileName(resume.getOriginalFileName())
					.fileFormat(resume.getFileFormat()).resumeId(resume.getId())
					.message("Resume is available for download").build();
		} else {
			log.info("No active resume found for download");
			return ResumeDownloadInfo.builder().available(false)
					.message("No resume is currently available for download. Please contact the administrator.")
					.build();
		}
	}

	@Override
	public ResumeEntity getResumeById(Long id) {
		log.debug("Fetching resume by ID: {}", id);
		return resumeRepository.findById(id).orElse(null);
	}

	@Override
	public List<ResumeResponse> getAllResumes() {
		log.debug("Fetching all resumes");
		List<ResumeEntity> resumes = resumeRepository.findAllByOrderByUploadedDateDesc();

		return resumes.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public boolean setActiveResume(Long id) {
		log.info("Setting resume as active with ID: {}", id);

		Optional<ResumeEntity> resumeOpt = resumeRepository.findById(id);
		if (resumeOpt.isEmpty()) {
			log.warn("Resume not found with ID: {}", id);
			return false;
		}

		try {
			// Deactivate all resumes
			resumeRepository.deactivateAllResumes();

			// Activate the selected resume
			ResumeEntity resume = resumeOpt.get();
			resume.setActive(true);
			resumeRepository.save(resume);

			log.info("Successfully set resume as active with ID: {}", id);
			return true;
		} catch (Exception e) {
			log.error("Error setting resume as active with ID: {}", id, e);
			return false;
		}
	}

	@Override
	@Transactional
	public boolean deleteResume(Long id) {
		log.info("Deleting resume with ID: {}", id);

		try {
			if (!resumeRepository.existsById(id)) {
				log.warn("Resume not found with ID: {}", id);
				throw new ResourceNotFoundException("Resume not found with ID: " + id);
			}

			resumeRepository.deleteById(id);
			log.info("Successfully deleted resume with ID: {}", id);
			return true;
		} catch (Exception e) {
			log.error("Error deleting resume with ID: {}", id, e);
			return false;
		}
	}

	@Override
	public String validateResumeFile(MultipartFile file) {
		// Check if file is empty
		if (file == null || file.isEmpty()) {
			return "Please select a file to upload";
		}

		// Check file size
		if (file.getSize() > MAX_FILE_SIZE) {
			return String.format("File size exceeds maximum limit of %s", formatFileSize(MAX_FILE_SIZE));
		}

		// Check content type
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			return "Invalid file type. Please upload PDF, DOC, or DOCX files only";
		}

		// Check file extension
		String originalFileName = file.getOriginalFilename();
		if (originalFileName == null || originalFileName.trim().isEmpty()) {
			return "Invalid file name";
		}

		String fileExtension = getFileExtension(originalFileName);
		if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			return "Invalid file extension. Please upload PDF, DOC, or DOCX files only";
		}

		// Additional security check for file content
		if (file.getSize() < 100) { // Too small to be a valid document
			return "File appears to be corrupted or invalid";
		}

		return null; // No validation errors
	}

	/**
	 * Map ResumeEntity to ResumeResponse (without file data)
	 */
	private ResumeResponse mapToResponse(ResumeEntity entity) {
		return ResumeResponse.builder().id(entity.getId()).fileName(entity.getFileName())
				.originalFileName(entity.getOriginalFileName()).fileFormat(entity.getFileFormat())
				.fileSize(entity.getFileSize()).fileSizeFormatted(formatFileSize(entity.getFileSize()))
				.contentType(entity.getContentType()).uploadedDate(entity.getUploadedDate()).isActive(entity.isActive())
				.build();
	}

	/**
	 * Generate unique file name to avoid conflicts
	 */
	private String generateUniqueFileName(String originalFileName, String extension) {
		String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
		String timestamp = String.valueOf(System.currentTimeMillis());
		String uuid = UUID.randomUUID().toString().substring(0, 8);

		return String.format("%s_%s_%s.%s", baseName, timestamp, uuid, extension);
	}

	/**
	 * Get file extension from filename
	 */
	private String getFileExtension(String fileName) {
		if (fileName == null || !fileName.contains(".")) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	/**
	 * Format file size to human readable format
	 */
	private String formatFileSize(long sizeInBytes) {
		if (sizeInBytes <= 0)
			return "0 B";

		final String[] units = new String[] { "B", "KB", "MB", "GB" };
		int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));

		return new DecimalFormat("#,##0.#").format(sizeInBytes / Math.pow(1024, digitGroups)) + " "
				+ units[digitGroups];
	}
}