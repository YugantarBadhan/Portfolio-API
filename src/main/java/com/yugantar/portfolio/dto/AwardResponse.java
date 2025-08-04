package com.yugantar.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardResponse {
	private Long id;
	private String awardName;
	private String description;
	private String awardCompanyName;
	private String awardLink;
	private String awardYear;
}