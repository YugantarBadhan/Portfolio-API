package com.yugantar.portfolio.service;

import java.util.List;

import com.yugantar.portfolio.dto.ProjectRequest;
import com.yugantar.portfolio.dto.ProjectResponse;

public interface ProjectService {
	List<ProjectResponse> getAllProjects();

	void createProject(ProjectRequest projectRequest);

	void updateProject(Long id, ProjectRequest request);

	void deleteProject(Long id);
}
