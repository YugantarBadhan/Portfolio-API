package com.yugantar.portfolio.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceResponse {
	private Long id;
	private String companyName;
	private String role;
	private LocalDate startDate;
	private LocalDate endDate;
	private boolean current;
	private String description;
	private List<String> skills;
}
