package com.yugantar.portfolio.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.yugantar.portfolio.dto.ResumeDownloadInfo;
import com.yugantar.portfolio.dto.ResumeResponse;
import com.yugantar.portfolio.dto.ResumeUploadResponse;
import com.yugantar.portfolio.entity.ResumeEntity;

public interface ResumeService {

	/**
	 * Upload a new resume file
	 * 
	 * @param file the resume file to upload
	 * @return upload response with success status and resume info
	 */
	ResumeUploadResponse uploadResume(MultipartFile file);

	/**
	 * Get the currently active resume for download
	 * 
	 * @return active resume entity or null if no active resume
	 */
	ResumeEntity getActiveResume();

	/**
	 * Get download info for the active resume
	 * 
	 * @return download info including availability status
	 */
	ResumeDownloadInfo getResumeDownloadInfo();

	/**
	 * Get resume by ID for preview/download
	 * 
	 * @param id resume ID
	 * @return resume entity or null if not found
	 */
	ResumeEntity getResumeById(Long id);

	/**
	 * Get all uploaded resumes (admin view)
	 * 
	 * @return list of resume responses without file data
	 */
	List<ResumeResponse> getAllResumes();

	/**
	 * Set a specific resume as active
	 * 
	 * @param id resume ID to activate
	 * @return success status
	 */
	boolean setActiveResume(Long id);

	/**
	 * Delete a resume
	 * 
	 * @param id resume ID to delete
	 * @return success status
	 */
	boolean deleteResume(Long id);

	/**
	 * Validate file format and size
	 * 
	 * @param file file to validate
	 * @return validation result message (null if valid)
	 */
	String validateResumeFile(MultipartFile file);
}