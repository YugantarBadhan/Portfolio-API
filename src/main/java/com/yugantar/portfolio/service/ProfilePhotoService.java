package com.yugantar.portfolio.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.yugantar.portfolio.dto.ProfilePhotoInfo;
import com.yugantar.portfolio.dto.ProfilePhotoResponse;
import com.yugantar.portfolio.dto.ProfilePhotoUploadResponse;
import com.yugantar.portfolio.entity.ProfilePhotoEntity;

public interface ProfilePhotoService {

	/**
	 * Upload a new profile photo
	 * 
	 * @param file the image file to upload
	 * @return upload response with success status and photo info
	 */
	ProfilePhotoUploadResponse uploadProfilePhoto(MultipartFile file);

	/**
	 * Get the currently active profile photo for display
	 * 
	 * @return active profile photo entity or null if no active photo
	 */
	ProfilePhotoEntity getActiveProfilePhoto();

	/**
	 * Get profile photo info for public access
	 * 
	 * @return profile photo info including availability status
	 */
	ProfilePhotoInfo getProfilePhotoInfo();

	/**
	 * Get profile photo by ID for serving
	 * 
	 * @param id photo ID
	 * @return profile photo entity or null if not found
	 */
	ProfilePhotoEntity getProfilePhotoById(Long id);

	/**
	 * Get all uploaded profile photos (admin view)
	 * 
	 * @return list of profile photo responses without image data
	 */
	List<ProfilePhotoResponse> getAllProfilePhotos();

	/**
	 * Set a specific profile photo as active
	 * 
	 * @param id photo ID to activate
	 * @return success status
	 */
	boolean setActiveProfilePhoto(Long id);

	/**
	 * Delete a profile photo
	 * 
	 * @param id photo ID to delete
	 * @return success status
	 */
	boolean deleteProfilePhoto(Long id);

	/**
	 * Validate image file format and size
	 * 
	 * @param file file to validate
	 * @return validation result message (null if valid)
	 */
	String validateProfilePhoto(MultipartFile file);

	/**
	 * Get image data for serving
	 * 
	 * @param id photo ID
	 * @return image data as byte array
	 */
	byte[] getImageData(Long id);

	/**
	 * Get active profile photo image data
	 * 
	 * @return active photo image data as byte array
	 */
	byte[] getActiveProfilePhotoImageData();
}