package com.yugantar.portfolio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequest {
	@NotBlank(message = "Skill name must not be blank")
	private String name;

	private String category;

	@Min(value = 0, message = "Proficiency must be between 0 and 5")
	@Max(value = 5, message = "Proficiency must be between 0 and 5")
	private Integer proficiency = 0;
}