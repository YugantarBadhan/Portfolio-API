package com.yugantar.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yugantar.portfolio.entity.ProfilePhotoEntity;

public interface ProfilePhotoRepository extends JpaRepository<ProfilePhotoEntity, Long> {

	// Find the currently active profile photo
	Optional<ProfilePhotoEntity> findByIsActiveTrue();

	// Find all profile photos ordered by upload date (latest first)
	List<ProfilePhotoEntity> findAllByOrderByUploadedDateDesc();

	// Deactivate all profile photos (to make a new one active)
	@Modifying
	@Query("UPDATE ProfilePhotoEntity p SET p.isActive = false")
	void deactivateAllPhotos();

	// Check if a photo with same file name exists
	boolean existsByOriginalFileName(String originalFileName);

	// Count total profile photos
	@Query("SELECT COUNT(p) FROM ProfilePhotoEntity p")
	long countTotalPhotos();

	// Get image data by ID (for serving images)
	@Query("SELECT p.imageData FROM ProfilePhotoEntity p WHERE p.id = :id")
	Optional<byte[]> findImageDataById(@Param("id") Long id);

	// Find photo info without image data (for listing)
	@Query("SELECT new com.yugantar.portfolio.entity.ProfilePhotoEntity(p.id, p.fileName, p.originalFileName, p.fileFormat, p.fileSize, p.contentType, null, p.uploadedDate, p.isActive, p.imageWidth, p.imageHeight) FROM ProfilePhotoEntity p WHERE p.id = :id")
	Optional<ProfilePhotoEntity> findPhotoInfoById(@Param("id") Long id);

	// Get active photo image data
	@Query("SELECT p.imageData FROM ProfilePhotoEntity p WHERE p.isActive = true")
	Optional<byte[]> findActivePhotoImageData();

	// Get active photo info without image data
	@Query("SELECT new com.yugantar.portfolio.entity.ProfilePhotoEntity(p.id, p.fileName, p.originalFileName, p.fileFormat, p.fileSize, p.contentType, null, p.uploadedDate, p.isActive, p.imageWidth, p.imageHeight) FROM ProfilePhotoEntity p WHERE p.isActive = true")
	Optional<ProfilePhotoEntity> findActivePhotoInfo();
}