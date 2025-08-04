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

import com.yugantar.portfolio.dto.CertificationRequest;
import com.yugantar.portfolio.dto.CertificationResponse;
import com.yugantar.portfolio.service.CertificationService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class CertificationController {

	@Autowired
	private CertificationService certificationService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	@GetMapping("/certifications")
	public ResponseEntity<List<CertificationResponse>> getAll() {
		return ResponseEntity.ok(certificationService.getAllCertifications());
	}

	@PostMapping("/create/certification")
	public ResponseEntity<String> create(@Valid @RequestBody CertificationRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized certification creation attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		certificationService.createCertification(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Certification created successfully");
	}

	@PutMapping("/update/certification/{id}")
	public ResponseEntity<String> update(@PathVariable Long id, @Valid @RequestBody CertificationRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized certification update attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		certificationService.updateCertification(id, request);
		return ResponseEntity.ok("Certification updated successfully");
	}

	@DeleteMapping("/delete/certification/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized certification delete attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		certificationService.deleteCertification(id);
		return ResponseEntity.ok("Certification deleted successfully");
	}
}
