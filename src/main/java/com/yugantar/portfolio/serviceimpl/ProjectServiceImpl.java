package com.yugantar.portfolio.serviceimpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jdi.request.DuplicateRequestException;
import com.yugantar.portfolio.dto.ProjectRequest;
import com.yugantar.portfolio.dto.ProjectResponse;
import com.yugantar.portfolio.entity.ProjectEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.ProjectRepository;
import com.yugantar.portfolio.service.ProjectService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Override
	public List<ProjectResponse> getAllProjects() {
		List<ProjectEntity> projects = projectRepository.findAll();

		if (projects.isEmpty()) {
			throw new ResourceNotFoundException("No projects found");
		}

		return projects.stream()
				.map(p -> ProjectResponse.builder().id(p.getId()).title(p.getTitle()).description(p.getDescription())
						.techStack(p.getTechStack()).githubLink(p.getGithubLink()).liveDemoLink(p.getLiveDemoLink())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public void createProject(ProjectRequest request) {
		List<ProjectEntity> existingProjects = projectRepository.findAllByTitleIgnoreCase(request.getTitle());

		if (!existingProjects.isEmpty()) {
			log.warn("Duplicate project creation attempted with title: {}", request.getTitle());
			throw new IllegalArgumentException("Project already exists with title: " + request.getTitle());
		}

		ProjectEntity project = ProjectEntity.builder().title(request.getTitle()).description(request.getDescription())
				.techStack(request.getTechStack()).githubLink(request.getGithubLink())
				.liveDemoLink(request.getLiveDemoLink()).build();

		projectRepository.save(project);
		log.info("Project saved: {}", project.getTitle());
	}

	@Override
	public void updateProject(Long id, ProjectRequest request) {
		ProjectEntity project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

		// Check if all fields are same
		boolean noChange = project.getTitle().equals(request.getTitle())
				&& project.getDescription().equals(request.getDescription())
				&& Objects.equals(project.getTechStack(), request.getTechStack())
				&& Objects.equals(project.getGithubLink(), request.getGithubLink())
				&& Objects.equals(project.getLiveDemoLink(), request.getLiveDemoLink());

		if (noChange) {
			log.warn("No changes detected in update for project: {}", id);
			throw new DuplicateRequestException("No changes detected. Project is already up to date.");
		}

		// Proceed with update
		project.setTitle(request.getTitle());
		project.setDescription(request.getDescription());
		project.setTechStack(request.getTechStack());
		project.setGithubLink(request.getGithubLink());
		project.setLiveDemoLink(request.getLiveDemoLink());

		projectRepository.save(project);
		log.info("Project updated: {}", project.getTitle());
	}

	@Override
	public void deleteProject(Long id) {
		ProjectEntity project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));

		projectRepository.delete(project);
		log.info("Deleted project with ID: {}", id);
	}

}
