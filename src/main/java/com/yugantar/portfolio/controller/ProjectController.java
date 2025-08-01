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

import com.yugantar.portfolio.dto.ProjectRequest;
import com.yugantar.portfolio.dto.ProjectResponse;
import com.yugantar.portfolio.service.ProjectService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@Slf4j
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Value("${admin.token}")
	private String ADMIN_TOKEN;

	// Public: Get all projects
	@GetMapping("/projects")
	public ResponseEntity<List<ProjectResponse>> getAllProjects() {
		List<ProjectResponse> projects = projectService.getAllProjects();
		return ResponseEntity.ok(projects);
	}

	// Admin: Create project
	@PostMapping("/create/project")
	public ResponseEntity<String> createProject(@Valid @RequestBody ProjectRequest projectDto,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized project creation attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Creating a new project with title: {}", projectDto.getTitle());
		projectService.createProject(projectDto);
		return ResponseEntity.status(HttpStatus.CREATED).body("Project created successfully");
	}

	// Admin: Update project
	@PutMapping("/update/project/{id}")
	public ResponseEntity<String> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest projectDto,
			@RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized project update attempt");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Updating project with ID: {}", id);
		projectService.updateProject(id, projectDto);
		return ResponseEntity.ok("Project updated successfully");
	}

	// Admin: Delete project
	@DeleteMapping("/delete/project/{id}")
	public ResponseEntity<String> deleteProject(@PathVariable Long id, @RequestHeader("X-ADMIN-TOKEN") String token) {
		if (!ADMIN_TOKEN.equals(token)) {
			log.warn("Unauthorized delete attempt with token: {}", token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
		}

		log.info("Deleting project with ID: {}", id);
		projectService.deleteProject(id);
		return ResponseEntity.ok("Project Deleted successfully");
	}
}
