package com.yugantar.portfolio.service;

import java.util.List;

import com.yugantar.portfolio.dto.EducationRequest;
import com.yugantar.portfolio.dto.EducationResponse;

public interface EducationService {
	void createEducation(EducationRequest request);

	void updateEducation(Long id, EducationRequest request);

	void deleteEducation(Long id);

	List<EducationResponse> getAllEducations();
}