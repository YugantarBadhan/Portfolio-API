package com.yugantar.portfolio.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
@Table(name = "experiences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String companyName;

	@Column(nullable = false)
	private String role;

	@Column(nullable = false)
	private LocalDate startDate;

	private LocalDate endDate;

	@Column(name = "is_current")
	private boolean current;
	
	@Column(columnDefinition = "TEXT")
	private String description;

	@ElementCollection
	private List<String> skills;
}
