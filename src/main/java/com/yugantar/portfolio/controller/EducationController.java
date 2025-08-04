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

import com.yugantar.portfolio.dto.EducationRequest;
import com.yugantar.portfolio.dto.EducationResponse;
import com.yugantar.portfolio.service.EducationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class EducationController {

	@Autowired
	private EducationService educationService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	@GetMapping("/educations")
	public ResponseEntity<List<EducationResponse>> getAllEducations() {
		List<EducationResponse> list = educationService.getAllEducations();
		return ResponseEntity.ok(list);
	}

	@PostMapping("/create/education")
	public ResponseEntity<String> createEducation(@Valid @RequestBody EducationRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		educationService.createEducation(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Education created successfully");
	}

	@PutMapping("/update/education/{id}")
	public ResponseEntity<String> updateEducation(@PathVariable Long id, @Valid @RequestBody EducationRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		educationService.updateEducation(id, request);
		return ResponseEntity.ok("Education updated successfully");
	}

	@DeleteMapping("/delete/education/{id}")
	public ResponseEntity<String> deleteEducation(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		educationService.deleteEducation(id);
		return ResponseEntity.ok("Education deleted successfully");
	}
}
