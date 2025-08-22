package com.yugantar.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUploadResponse {
	private boolean success;
	private String message;
	private ResumeResponse resumeInfo;
	private String errorCode; // For specific error handling on frontend
}