package com.yugantar.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePhotoUploadResponse {
	private boolean success;
	private String message;
	private ProfilePhotoResponse photoInfo;
	private String errorCode; // For specific error handling on frontend
}