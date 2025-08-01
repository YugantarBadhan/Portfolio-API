package com.yugantar.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {

	@NotBlank(message = "Project name is required")
	@Size(max = 400, message = "Project name can be at most 400 characters")
	private String title;

	@NotBlank(message = "Description is required")
	private String description;

	private String techStack;
	private String githubLink;
	private String liveDemoLink;

}