package com.yugantar.portfolio.service;

import java.util.List;

import com.yugantar.portfolio.dto.SkillRequest;
import com.yugantar.portfolio.dto.SkillResponse;

public interface SkillService {
	SkillResponse createSkill(SkillRequest request);

	SkillResponse updateSkill(Long id, SkillRequest request);

	void deleteSkill(Long id);

	List<SkillResponse> getAllSkills();
}
