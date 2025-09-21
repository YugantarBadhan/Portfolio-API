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

import com.yugantar.portfolio.dto.ProfilePhotoInfo;
import com.yugantar.portfolio.dto.ProfilePhotoResponse;
import com.yugantar.portfolio.dto.ProfilePhotoUploadResponse;
import com.yugantar.portfolio.entity.ProfilePhotoEntity;
import com.yugantar.portfolio.service.ProfilePhotoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class ProfilePhotoController {

	private final ProfilePhotoService profilePhotoService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	/**
	 * Public endpoint to get profile photo info
	 */
	@GetMapping("/profile-photo/info")
	public ResponseEntity<ProfilePhotoInfo> getProfilePhotoInfo() {
		log.debug("Getting profile photo info");
		ProfilePhotoInfo photoInfo = profilePhotoService.getProfilePhotoInfo();
		return ResponseEntity.ok(photoInfo);
	}

	/**
	 * Public endpoint to view profile photo
	 */
	@GetMapping("/profile-photo/view/{id}")
	public ResponseEntity<Resource> viewProfilePhoto(@PathVariable Long id) {
		log.info("Profile photo view requested for ID: {}", id);

		ProfilePhotoEntity photo = profilePhotoService.getProfilePhotoById(id);
		if (photo == null) {
			log.warn("Profile photo not found for view with ID: {}", id);
			return ResponseEntity.notFound().build();
		}

		try {
			ByteArrayResource resource = new ByteArrayResource(photo.getImageData());

			String contentType = photo.getContentType();
			if (contentType == null) {
				contentType = "image/jpeg"; // default fallback
			}

			log.info("Serving profile photo: {} ({})", photo.getOriginalFileName(), photo.getFileFormat());

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(photo.getFileSize()).header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400") // Cache
																													// for
																													// 1
																													// day
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getOriginalFileName() + "\"")
					.body(resource);

		} catch (Exception e) {
			log.error("Error serving profile photo for ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Public endpoint to view active profile photo
	 */
	@GetMapping("/profile-photo/active")
	public ResponseEntity<Resource> viewActiveProfilePhoto() {
		log.info("Active profile photo view requested");

		ProfilePhotoEntity activePhoto = profilePhotoService.getActiveProfilePhoto();
		if (activePhoto == null) {
			log.warn("No active profile photo available for view");
			return ResponseEntity.notFound().build();
		}

		try {
			ByteArrayResource resource = new ByteArrayResource(activePhoto.getImageData());

			String contentType = activePhoto.getContentType();
			if (contentType == null) {
				contentType = "image/jpeg"; // default fallback
			}

			log.info("Serving active profile photo: {} ({})", activePhoto.getOriginalFileName(),
					activePhoto.getFileFormat());

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(activePhoto.getFileSize()).header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600") // Cache
																														// for
																														// 1
																														// hour
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"inline; filename=\"" + activePhoto.getOriginalFileName() + "\"")
					.body(resource);

		} catch (Exception e) {
			log.error("Error serving active profile photo", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	/**
	 * Admin endpoint to upload profile photo
	 */
	@PostMapping("/profile-photo/upload")
	public ResponseEntity<ProfilePhotoUploadResponse> uploadProfilePhoto(@RequestParam("file") MultipartFile file,
			@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized profile photo upload attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ProfilePhotoUploadResponse.builder()
					.success(false).message("Access denied").errorCode("UNAUTHORIZED").build());
		}

		log.info("Admin profile photo upload requested for file: {}", file.getOriginalFilename());

		ProfilePhotoUploadResponse response = profilePhotoService.uploadProfilePhoto(file);

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
	 * Admin endpoint to get all profile photos
	 */
	@GetMapping("/profile-photos")
	public ResponseEntity<List<ProfilePhotoResponse>> getAllProfilePhotos(
			@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized access to profile photos list");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.debug("Admin requesting all profile photos list");
		List<ProfilePhotoResponse> photos = profilePhotoService.getAllProfilePhotos();
		return ResponseEntity.ok(photos);
	}

	/**
	 * Admin endpoint to set active profile photo
	 */
	@PutMapping("/profile-photo/{id}/activate")
	public ResponseEntity<String> setActiveProfilePhoto(@PathVariable Long id,
			@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized attempt to set active profile photo");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Admin setting profile photo as active with ID: {}", id);

		boolean success = profilePhotoService.setActiveProfilePhoto(id);
		if (success) {
			return ResponseEntity.ok("Profile photo set as active successfully");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile photo not found with ID: " + id);
		}
	}

	/**
	 * Admin endpoint to delete profile photo
	 */
	@DeleteMapping("/profile-photo/{id}")
	public ResponseEntity<String> deleteProfilePhoto(@PathVariable Long id,
			@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized attempt to delete profile photo");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Admin deleting profile photo with ID: {}", id);

		boolean success = profilePhotoService.deleteProfilePhoto(id);
		if (success) {
			return ResponseEntity.ok("Profile photo deleted successfully");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile photo not found with ID: " + id);
		}
	}

	/**
	 * Admin endpoint to download profile photo (for backup purposes)
	 */
	@GetMapping("/profile-photo/download/{id}")
	public ResponseEntity<Resource> downloadProfilePhoto(@PathVariable Long id,
			@RequestHeader("X-ADMIN-TOKEN") String token) {

		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized attempt to download profile photo");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		log.info("Admin downloading profile photo with ID: {}", id);

		ProfilePhotoEntity photo = profilePhotoService.getProfilePhotoById(id);
		if (photo == null) {
			log.warn("Profile photo not found for download with ID: {}", id);
			return ResponseEntity.notFound().build();
		}

		try {
			ByteArrayResource resource = new ByteArrayResource(photo.getImageData());

			String contentType = photo.getContentType();
			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			log.info("Serving profile photo download: {} ({})", photo.getOriginalFileName(), photo.getFileFormat());

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(photo.getFileSize()).header(HttpHeaders.CONTENT_DISPOSITION,
							"attachment; filename=\"" + photo.getOriginalFileName() + "\"")
					.body(resource);

		} catch (Exception e) {
			log.error("Error serving profile photo download for ID: {}", id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}