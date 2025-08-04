package com.yugantar.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationRequest {

	@NotBlank(message = "Degree/Qualification is required")
	private String degree;

	@NotBlank(message = "Field of Study/Major is required")
	private String field;

	@NotBlank(message = "University/Board is required")
	private String university;

	@NotBlank(message = "Institute is required")
	private String institute;

	private String location;

	@NotBlank(message = "Start date is required")
	private String startDate;

	private String endDate;

	private boolean currentStudying;

	@NotBlank(message = "Grade/CGPA/Percentage is required")
	private String grade;

	@NotBlank(message = "Education type is required")
	private String educationType;

	private String description;
}
