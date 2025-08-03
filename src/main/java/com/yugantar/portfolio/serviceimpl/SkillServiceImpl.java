package com.yugantar.portfolio.serviceimpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jdi.request.DuplicateRequestException;
import com.yugantar.portfolio.dto.SkillRequest;
import com.yugantar.portfolio.dto.SkillResponse;
import com.yugantar.portfolio.entity.SkillEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.SkillRepository;
import com.yugantar.portfolio.service.SkillService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkillServiceImpl implements SkillService {

	@Autowired
	private SkillRepository skillRepo;

	@Override
	public SkillResponse createSkill(SkillRequest request) {
		log.info("Creating skill: {}", request.getName());

		if (skillRepo.findByNameIgnoreCase(request.getName()).isPresent()) {
			log.warn("Duplicate skill creation attempt: {}", request.getName());
			throw new DuplicateRequestException("Skill with name already exists: " + request.getName());
		}

		SkillEntity skill = SkillEntity.builder().name(request.getName()).category(request.getCategory())
				.proficiency(Optional.ofNullable(request.getProficiency()).orElse(0)).build();

		SkillEntity saved = skillRepo.save(skill);
		return mapToResponse(saved);
	}

	@Override
	public SkillResponse updateSkill(Long id, SkillRequest request) {
		log.info("Updating skill with ID: {}", id);

		SkillEntity existing = skillRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

		int newProf = Optional.ofNullable(request.getProficiency()).orElse(0);
		boolean isSame = existing.getName().equalsIgnoreCase(request.getName())
				&& Objects.equals(existing.getCategory(), request.getCategory())
				&& existing.getProficiency() == newProf;

		if (isSame) {
			log.warn("No change detected for skill update id: {}", id);
			throw new IllegalArgumentException("No changes detected in the request.");
		}

		existing.setName(request.getName());
		existing.setCategory(request.getCategory());
		existing.setProficiency(newProf);

		return mapToResponse(skillRepo.save(existing));
	}

	@Override
	public void deleteSkill(Long id) {
		log.info("Deleting skill with ID: {}", id);

		SkillEntity skill = skillRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
		skillRepo.delete(skill);
	}

	@Override
	public List<SkillResponse> getAllSkills() {
		log.info("Fetching all skills");
		List<SkillEntity> list = skillRepo.findAll();
		if (list.isEmpty()) {
			throw new ResourceNotFoundException("No skills available to fetch.");
		}
		return list.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	private SkillResponse mapToResponse(SkillEntity entity) {
		return SkillResponse.builder().id(entity.getId()).name(entity.getName()).category(entity.getCategory())
				.proficiency(entity.getProficiency()).build();
	}
}
