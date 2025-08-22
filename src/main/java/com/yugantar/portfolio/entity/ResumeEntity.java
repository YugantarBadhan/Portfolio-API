package com.yugantar.portfolio.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 500)
	private String fileName;

	@Column(nullable = false, length = 100)
	private String originalFileName;

	@Column(nullable = false)
	private String fileFormat; // PDF, DOCX, DOC

	@Column(nullable = false)
	private Long fileSize; // in bytes

	@Column(nullable = false, length = 100)
	private String contentType;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGBLOB")
	private byte[] fileData;

	@Column(nullable = false)
	private LocalDateTime uploadedDate;

	@Column(nullable = false)
	private boolean isActive = true; // Only one resume can be active at a time

	@PrePersist
	protected void onCreate() {
		if (uploadedDate == null) {
			uploadedDate = LocalDateTime.now();
		}
	}
}