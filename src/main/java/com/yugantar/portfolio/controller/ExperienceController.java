package com.yugantar.portfolio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yugantar.portfolio.dto.ExperienceRequest;
import com.yugantar.portfolio.dto.ExperienceResponse;
import com.yugantar.portfolio.service.ExperienceService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class ExperienceController {

	@Autowired
	private ExperienceService experienceService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	// Public: Get all experiences
	@GetMapping("/experiences")
	public ResponseEntity<List<ExperienceResponse>> getAllExperiences() {
		List<ExperienceResponse> experiences = experienceService.getAllExperiences();
		return ResponseEntity.ok(experiences);
	}

	// Admin: Create experience
	@PostMapping("/create/experience")
	public ResponseEntity<String> createExperience(@Valid @RequestBody ExperienceRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized experience creation attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Creating a new experience at company: {}", request.getCompanyName());
		experienceService.createExperience(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Experience created successfully");
	}

	// Admin: Update experience
	@PutMapping("/update/experience/{id}")
	public ResponseEntity<String> updateExperience(@PathVariable Long id, @Valid @RequestBody ExperienceRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized experience update attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Updating experience with ID: {}", id);
		experienceService.updateExperience(id, request);
		return ResponseEntity.ok("Experience updated successfully");
	}

	// Admin: Delete experience
	@DeleteMapping("/delete/experience/{id}")
	public ResponseEntity<String> deleteExperience(@PathVariable Long id,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized experience delete attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Deleting experience with ID: {}", id);
		experienceService.deleteExperience(id);
		return ResponseEntity.ok("Experience deleted successfully");
	}
}
