package com.yugantar.portfolio.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeResponse {
	private Long id;
	private String fileName;
	private String originalFileName;
	private String fileFormat;
	private Long fileSize;
	private String fileSizeFormatted; // Human readable size (e.g., "2.5 MB")
	private String contentType;
	private LocalDateTime uploadedDate;
	private boolean isActive;
}