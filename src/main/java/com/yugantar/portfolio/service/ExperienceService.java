package com.yugantar.portfolio.service;

import java.util.List;

import com.yugantar.portfolio.dto.ExperienceRequest;
import com.yugantar.portfolio.dto.ExperienceResponse;

public interface ExperienceService {

	void createExperience(ExperienceRequest request);

	void updateExperience(Long id, ExperienceRequest request);

	void deleteExperience(Long id);

	List<ExperienceResponse> getAllExperiences();
}
