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

import com.yugantar.portfolio.dto.SkillRequest;
import com.yugantar.portfolio.dto.SkillResponse;
import com.yugantar.portfolio.service.SkillService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class SkillController {

	@Autowired
	private SkillService skillService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	// Public: Get all skills
	@GetMapping("/skills")
	public ResponseEntity<List<SkillResponse>> getAllSkills() {
		List<SkillResponse> skills = skillService.getAllSkills();
		return ResponseEntity.ok(skills);
	}

	// Admin: Create skill
	@PostMapping("/create/skill")
	public ResponseEntity<String> createSkill(@Valid @RequestBody SkillRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized skill creation attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Creating new skill: {}", request.getName());
		skillService.createSkill(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("Skill created successfully");
	}

	// Admin: Update skill
	@PutMapping("/update/skill/{id}")
	public ResponseEntity<String> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillRequest request,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized skill update attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Updating skill with ID: {}", id);
		skillService.updateSkill(id, request);
		return ResponseEntity.ok("Skill updated successfully");
	}

	// Admin: Delete skill
	@DeleteMapping("/delete/skill/{id}")
	public ResponseEntity<String> deleteSkill(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized skill deletion attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Deleting skill with ID: {}", id);
		skillService.deleteSkill(id);
		return ResponseEntity.ok("Skill deleted successfully");
	}
}
