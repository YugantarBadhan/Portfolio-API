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

import com.yugantar.portfolio.dto.AwardRequest;
import com.yugantar.portfolio.dto.AwardResponse;
import com.yugantar.portfolio.service.AwardService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class AwardController {

	@Autowired
	private AwardService awardService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	@GetMapping("/awards")
	public ResponseEntity<List<AwardResponse>> getAllAwards() {
		List<AwardResponse> awards = awardService.getAllAwards();
		return ResponseEntity.ok(awards);
	}

	@PostMapping("/create/award")
	public ResponseEntity<String> createAward(@Valid @RequestBody AwardRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized award creation attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		log.info("Creating new award: {}", request.getAwardName());
		awardService.createAward(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Award created successfully");
	}

	@PutMapping("/update/award/{id}")
	public ResponseEntity<String> updateAward(@PathVariable Long id, @Valid @RequestBody AwardRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized award update attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		log.info("Updating award with ID: {}", id);
		awardService.updateAward(id, request);
		return ResponseEntity.ok("Award updated successfully");
	}

	@DeleteMapping("/delete/award/{id}")
	public ResponseEntity<String> deleteAward(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized award deletion attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}
		log.info("Deleting award with ID: {}", id);
		awardService.deleteAward(id);
		return ResponseEntity.ok("Award deleted successfully");
	}
}