package com.yugantar.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.yugantar.portfolio.dto.ResumeDownloadInfo;
import com.yugantar.portfolio.dto.ResumeResponse;
import com.yugantar.portfolio.dto.ResumeUploadResponse;
import com.yugantar.portfolio.entity.ResumeEntity;
import com.yugantar.portfolio.service.ResumeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

	private final ResumeService resumeService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	/**
	 * Public endpoint to get resume download info
	 */
	@GetMapping("/resume/download-info")
	public ResponseEntity<ResumeDownloadInfo> getResumeDownloadInfo() {
		log.debug("Getting resume download info");
		ResumeDownloadInfo downloadInfo = resumeService.getResumeDownloadInfo();
		return ResponseEntity.ok(downloadInfo);
	}

	/**
	 * Public endpoint to download the active resume
	 */
	@GetMapping("/resume/download")
	public ResponseEntity<Resource> downloadResume() {
		log.info("Resume download requested");

		ResumeEntity activeResume = resumeService.getActiveResume();
		if (activeResume == null) {
			log.warn("No active resume available for download");
			return ResponseEntity.notFound().build();
		}

		try {
			ByteArrayResource resource = new ByteArrayResource(activeResume.getFileData());

			String contentType = activeResume.getContentType();
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			log.info("Serving resume download: {} ({})", activeResume.getOriginalFileName(),
					activeResume.getFileFormat());

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(activeResume.getFileSize()).header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + activeResume.getOriginalFileName() + "\"")
					.body(resource);

		} catch (Exception e) {
			log.error("Error serving resume download", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Public endpoint to preview resume (opens in new tab)
	 */
	@GetMapping("/resume/preview/{id}")
	public ResponseEntity<Resource> previewResume(@PathVariable Long id) {
		log.info("Resume preview requested for ID: {}", id);

		ResumeEntity resume = resumeService.getResumeById(id);
		if (resume == null) {
			log.warn("Resume not found for preview with ID: {}", id);
			return ResponseEntity.notFound().build();
		}

		try {
			ByteArrayResource resource = new ByteArrayResource(resume.getFileData());

			String contentType = resume.getContentType();
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			log.info("Serving resume preview: {} ({})", resume.getOriginalFileName(), resume.getFileFormat());

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(resume.getFileSize()).header(HttpHeaders.CONTENT_DISPOSITION,
							"inline; filename=\"" + resume.getOriginalFileName() + "\"")
					.body(resource);

		} catch (Exception e) {
			log.error("Error serving resume preview for ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Admin endpoint to upload resume
	 */
	@PostMapping("/resume/upload")
	public ResponseEntity<ResumeUploadResponse> uploadResume(@RequestParam("file") MultipartFile file,
			@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized resume upload attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResumeUploadResponse.builder().success(false)
					.message("Access denied").errorCode("UNAUTHORIZED").build());
		}

		log.info("Admin resume upload requested for file: {}", file.getOriginalFilename());

		ResumeUploadResponse response = resumeService.uploadResume(file);

		if (response.isSuccess()) {
			return ResponseEntity.ok(response);
		} else {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			if ("FILE_READ_ERROR".equals(response.getErrorCode()) || "INTERNAL_ERROR".equals(response.getErrorCode())) {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
			return ResponseEntity.status(status).body(response);
		}
	}

	/**
	 * Admin endpoint to get all resumes
	 */
	@GetMapping("/resumes")
	public ResponseEntity<List<ResumeResponse>> getAllResumes(@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized access to resumes list");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.debug("Admin requesting all resumes list");
		List<ResumeResponse> resumes = resumeService.getAllResumes();
		return ResponseEntity.ok(resumes);
	}

	/**
	 * Admin endpoint to set active resume
	 */
	@PutMapping("/resume/{id}/activate")
	public ResponseEntity<String> setActiveResume(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized attempt to set active resume");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Admin setting resume as active with ID: {}", id);

		boolean success = resumeService.setActiveResume(id);
		if (success) {
			return ResponseEntity.ok("Resume set as active successfully");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resume not found with ID: " + id);
		}
	}

	/**
	 * Admin endpoint to delete resume
	 */
	@DeleteMapping("/resume/{id}")
	public ResponseEntity<String> deleteResume(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized attempt to delete resume");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Admin deleting resume with ID: {}", id);

		boolean success = resumeService.deleteResume(id);
		if (success) {
			return ResponseEntity.ok("Resume deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resume not found with ID: " + id);
		}
	}
}