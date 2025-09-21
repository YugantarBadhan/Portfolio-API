package com.yugantar.portfolio.serviceimpl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.yugantar.portfolio.dto.ProfilePhotoInfo;
import com.yugantar.portfolio.dto.ProfilePhotoResponse;
import com.yugantar.portfolio.dto.ProfilePhotoUploadResponse;
import com.yugantar.portfolio.entity.ProfilePhotoEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.ProfilePhotoRepository;
import com.yugantar.portfolio.service.ProfilePhotoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePhotoServiceImpl implements ProfilePhotoService {

	private final ProfilePhotoRepository profilePhotoRepository;

	// Allowed image content types
	private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png",
			"image/webp");

	// Allowed file extensions
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

	// Maximum file size: 5MB
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

	// Minimum image dimensions
	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 100;

	// Maximum image dimensions
	private static final int MAX_WIDTH = 4000;
	private static final int MAX_HEIGHT = 4000;

	@Override
	@Transactional
	public ProfilePhotoUploadResponse uploadProfilePhoto(MultipartFile file) {
		log.info("Starting profile photo upload process for file: {}", file.getOriginalFilename());

		try {
			// Validate file
			String validationError = validateProfilePhoto(file);
			if (validationError != null) {
				log.warn("Profile photo upload validation failed: {}", validationError);
				return ProfilePhotoUploadResponse.builder().success(false).message(validationError)
						.errorCode("VALIDATION_ERROR").build();
			}

			// Get image dimensions
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
			if (image == null) {
				log.warn("Unable to read image file: {}", file.getOriginalFilename());
				return ProfilePhotoUploadResponse.builder().success(false)
						.message("Invalid image file. Please upload a valid image.").errorCode("INVALID_IMAGE").build();
			}

			int width = image.getWidth();
			int height = image.getHeight();

			// Validate image dimensions
			if (width < MIN_WIDTH || height < MIN_HEIGHT) {
				return ProfilePhotoUploadResponse.builder().success(false).message(String
						.format("Image dimensions too small. Minimum size is %dx%d pixels.", MIN_WIDTH, MIN_HEIGHT))
						.errorCode("DIMENSIONS_TOO_SMALL").build();
			}

			if (width > MAX_WIDTH || height > MAX_HEIGHT) {
				return ProfilePhotoUploadResponse.builder().success(false).message(String
						.format("Image dimensions too large. Maximum size is %dx%d pixels.", MAX_WIDTH, MAX_HEIGHT))
						.errorCode("DIMENSIONS_TOO_LARGE").build();
			}

			// Generate unique file name
			String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
			String fileExtension = getFileExtension(originalFileName);
			String uniqueFileName = generateUniqueFileName(originalFileName, fileExtension);

			// Deactivate all existing profile photos
			profilePhotoRepository.deactivateAllPhotos();
			log.info("Deactivated all existing profile photos");

			// Create profile photo entity
			ProfilePhotoEntity photoEntity = ProfilePhotoEntity.builder().fileName(uniqueFileName)
					.originalFileName(originalFileName).fileFormat(fileExtension.toUpperCase()).fileSize(file.getSize())
					.contentType(file.getContentType()).imageData(file.getBytes()).uploadedDate(LocalDateTime.now())
					.isActive(true).imageWidth(width).imageHeight(height).build();

			// Save to database
			ProfilePhotoEntity savedPhoto = profilePhotoRepository.save(photoEntity);
			log.info("Profile photo uploaded successfully with ID: {}, Size: {} bytes, Dimensions: {}x{}",
					savedPhoto.getId(), savedPhoto.getFileSize(), width, height);

			// Create response
			ProfilePhotoResponse photoResponse = mapToResponse(savedPhoto);

			return ProfilePhotoUploadResponse.builder().success(true).message("Profile photo uploaded successfully")
					.photoInfo(photoResponse).build();

		} catch (IOException e) {
			log.error("Error reading image file during profile photo upload", e);
			return ProfilePhotoUploadResponse.builder().success(false)
					.message("Error reading image file. Please try again.").errorCode("FILE_READ_ERROR").build();
		} catch (Exception e) {
			log.error("Unexpected error during profile photo upload", e);
			return ProfilePhotoUploadResponse.builder().success(false)
					.message("An unexpected error occurred during upload. Please try again.")
					.errorCode("INTERNAL_ERROR").build();
		}
	}

	@Override
	public ProfilePhotoEntity getActiveProfilePhoto() {
		log.debug("Fetching active profile photo");
		return profilePhotoRepository.findByIsActiveTrue().orElse(null);
	}

	@Override
	public ProfilePhotoInfo getProfilePhotoInfo() {
		log.debug("Getting profile photo info");

		Optional<ProfilePhotoEntity> activePhoto = profilePhotoRepository.findActivePhotoInfo();

		if (activePhoto.isPresent()) {
			ProfilePhotoEntity photo = activePhoto.get();
			return ProfilePhotoInfo.builder().available(true).imageUrl("/api/profile-photo/view/" + photo.getId())
					.fileName(photo.getOriginalFileName()).fileFormat(photo.getFileFormat()).photoId(photo.getId())
					.imageWidth(photo.getImageWidth()).imageHeight(photo.getImageHeight())
					.message("Profile photo is available").build();
		} else {
			log.info("No active profile photo found");
			return ProfilePhotoInfo.builder().available(false).message("No profile photo is currently available.")
					.build();
		}
	}

	@Override
	public ProfilePhotoEntity getProfilePhotoById(Long id) {
		log.debug("Fetching profile photo by ID: {}", id);
		return profilePhotoRepository.findById(id).orElse(null);
	}

	@Override
	public List<ProfilePhotoResponse> getAllProfilePhotos() {
		log.debug("Fetching all profile photos");
		List<ProfilePhotoEntity> photos = profilePhotoRepository.findAllByOrderByUploadedDateDesc();

		return photos.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public boolean setActiveProfilePhoto(Long id) {
		log.info("Setting profile photo as active with ID: {}", id);

		Optional<ProfilePhotoEntity> photoOpt = profilePhotoRepository.findById(id);
		if (photoOpt.isEmpty()) {
			log.warn("Profile photo not found with ID: {}", id);
			return false;
		}

		try {
			// Deactivate all photos
			profilePhotoRepository.deactivateAllPhotos();

			// Activate the selected photo
			ProfilePhotoEntity photo = photoOpt.get();
			photo.setActive(true);
			profilePhotoRepository.save(photo);

			log.info("Successfully set profile photo as active with ID: {}", id);
			return true;
		} catch (Exception e) {
			log.error("Error setting profile photo as active with ID: {}", id, e);
			return false;
		}
	}

	@Override
	@Transactional
	public boolean deleteProfilePhoto(Long id) {
		log.info("Deleting profile photo with ID: {}", id);

		try {
			if (!profilePhotoRepository.existsById(id)) {
				log.warn("Profile photo not found with ID: {}", id);
				throw new ResourceNotFoundException("Profile photo not found with ID: " + id);
			}

			profilePhotoRepository.deleteById(id);
			log.info("Successfully deleted profile photo with ID: {}", id);
			return true;
		} catch (Exception e) {
			log.error("Error deleting profile photo with ID: {}", id, e);
			return false;
		}
	}

	@Override
	public String validateProfilePhoto(MultipartFile file) {
		// Check if file is empty
		if (file == null || file.isEmpty()) {
			return "Please select an image file to upload";
		}

		// Check file size
		if (file.getSize() > MAX_FILE_SIZE) {
			return String.format("File size exceeds maximum limit of %s", formatFileSize(MAX_FILE_SIZE));
		}

		// Check content type
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			return "Invalid file type. Please upload JPG, PNG, or WebP images only";
		}

		// Check file extension
		String originalFileName = file.getOriginalFilename();
		if (originalFileName == null || originalFileName.trim().isEmpty()) {
			return "Invalid file name";
		}

		String fileExtension = getFileExtension(originalFileName);
		if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			return "Invalid file extension. Please upload JPG, PNG, or WebP images only";
		}

		// Additional security check for file content
		if (file.getSize() < 100) { // Too small to be a valid image
			return "File appears to be corrupted or invalid";
		}

		return null; // No validation errors
	}

	@Override
	public byte[] getImageData(Long id) {
		log.debug("Fetching image data for ID: {}", id);
		return profilePhotoRepository.findImageDataById(id).orElse(null);
	}

	@Override
	public byte[] getActiveProfilePhotoImageData() {
		log.debug("Fetching active profile photo image data");
		return profilePhotoRepository.findActivePhotoImageData().orElse(null);
	}

	/**
	 * Map ProfilePhotoEntity to ProfilePhotoResponse (without image data)
	 */
	private ProfilePhotoResponse mapToResponse(ProfilePhotoEntity entity) {
		return ProfilePhotoResponse.builder().id(entity.getId()).fileName(entity.getFileName())
				.originalFileName(entity.getOriginalFileName()).fileFormat(entity.getFileFormat())
				.fileSize(entity.getFileSize()).fileSizeFormatted(formatFileSize(entity.getFileSize()))
				.contentType(entity.getContentType()).uploadedDate(entity.getUploadedDate()).isActive(entity.isActive())
				.imageWidth(entity.getImageWidth()).imageHeight(entity.getImageHeight())
				.imageUrl("/api/profile-photo/view/" + entity.getId()).build();
	}

	/**
	 * Generate unique file name to avoid conflicts
	 */
	private String generateUniqueFileName(String originalFileName, String extension) {
		String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
		String timestamp = String.valueOf(System.currentTimeMillis());
		String uuid = UUID.randomUUID().toString().substring(0, 8);

		return String.format("profile_%s_%s_%s.%s", baseName, timestamp, uuid, extension);
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