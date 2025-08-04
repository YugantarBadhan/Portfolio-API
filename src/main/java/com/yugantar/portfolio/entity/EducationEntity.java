package com.yugantar.portfolio.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String degree;

	@Column(nullable = false)
	private String field;

	@Column(nullable = false)
	private String university;

	@Column(nullable = false)
	private String institute;

	private String location;

	@Column(nullable = false)
	private String startDate;

	private String endDate;

	private boolean currentStudying;

	@Column(nullable = false)
	private String grade;

	@Column(nullable = false)
	private String educationType;

	@Column(columnDefinition = "TEXT")
	private String description;
}