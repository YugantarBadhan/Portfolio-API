package com.yugantar.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfilePhotoInfo {
	private boolean available;
	private String imageUrl;
	private String fileName;
	private String fileFormat;
	private String message;
	private Long photoId;
	private Integer imageWidth;
	private Integer imageHeight;
}