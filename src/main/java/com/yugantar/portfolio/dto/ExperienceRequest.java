package com.yugantar.portfolio.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceRequest {

	@NotBlank(message = "Company name is required")
	private String companyName;

	@NotBlank(message = "Role is required")
	private String role;

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	private LocalDate endDate;

	private boolean current;

	private String description;

	private List<String> skills;
}
