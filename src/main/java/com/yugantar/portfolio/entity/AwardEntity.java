package com.yugantar.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "awards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Award name must not be blank")
	@Column(nullable = false)
	private String awardName;

	@NotBlank(message = "Description must not be blank")
	@Column(nullable = false)
	private String description;

	@NotBlank(message = "Award company name must not be blank")
	@Column(nullable = false)
	private String awardCompanyName;

	private String awardLink;
	private String awardYear;
}
