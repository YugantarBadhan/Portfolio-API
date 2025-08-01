package com.yugantar.portfolio.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

	private Long id;
	private String title;
	private String description;
	private String techStack;
	private String githubLink;
	private String liveDemoLink;
}
