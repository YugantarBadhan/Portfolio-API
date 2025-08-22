package com.yugantar.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yugantar.portfolio.entity.ResumeEntity;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {

	// Find the currently active resume
	Optional<ResumeEntity> findByIsActiveTrue();

	// Find all resumes ordered by upload date (latest first)
	List<ResumeEntity> findAllByOrderByUploadedDateDesc();

	// Deactivate all resumes (to make a new one active)
	@Modifying
	@Query("UPDATE ResumeEntity r SET r.isActive = false")
	void deactivateAllResumes();

	// Check if a resume with same file name exists
	boolean existsByOriginalFileName(String originalFileName);

	// Count total resumes
	@Query("SELECT COUNT(r) FROM ResumeEntity r")
	long countTotalResumes();

	// Get file data by ID (for downloading)
	@Query("SELECT r.fileData FROM ResumeEntity r WHERE r.id = :id")
	Optional<byte[]> findFileDataById(@Param("id") Long id);

	// Find resume info without file data (for listing)
	@Query("SELECT new com.yugantar.portfolio.entity.ResumeEntity(r.id, r.fileName, r.originalFileName, r.fileFormat, r.fileSize, r.contentType, null, r.uploadedDate, r.isActive) FROM ResumeEntity r WHERE r.id = :id")
	Optional<ResumeEntity> findResumeInfoById(@Param("id") Long id);
}