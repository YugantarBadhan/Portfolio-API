package com.yugantar.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AwardRequest {
	@NotBlank(message = "Award name must not be blank")
	private String awardName;

	@NotBlank(message = "Description must not be blank")
	private String description;

	@NotBlank(message = "Award company name must not be blank")
	private String awardCompanyName;

	private String awardLink;
	private String awardYear;
}
