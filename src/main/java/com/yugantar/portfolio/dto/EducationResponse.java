package com.yugantar.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationResponse {
	private Long id;
	private String degree;
	private String field;
	private String university;
	private String institute;
	private String location;
	private String startDate;
	private String endDate;
	private boolean currentStudying;
	private String grade;
	private String educationType;
	private String description;
}
