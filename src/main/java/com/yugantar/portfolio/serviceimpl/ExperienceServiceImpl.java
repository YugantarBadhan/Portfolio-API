package com.yugantar.portfolio.serviceimpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yugantar.portfolio.dto.ExperienceRequest;
import com.yugantar.portfolio.dto.ExperienceResponse;
import com.yugantar.portfolio.entity.ExperienceEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.ExperienceRepository;
import com.yugantar.portfolio.service.ExperienceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienceServiceImpl implements ExperienceService {

	private final ExperienceRepository experienceRepository;

	@Override
	public void createExperience(ExperienceRequest request) {
		validateDates(request.getStartDate(), request.getEndDate(), request.isCurrent());

		LocalDate start = request.getStartDate();
		LocalDate end = request.isCurrent() ? LocalDate.now() : request.getEndDate();

		List<ExperienceEntity> all = experienceRepository.findAll();
		for (ExperienceEntity exp : all) {
			LocalDate existingStart = exp.getStartDate();
			LocalDate existingEnd = exp.isCurrent() ? LocalDate.now() : exp.getEndDate();

			boolean overlaps = !(end.isBefore(existingStart) || start.isAfter(existingEnd));
			if (overlaps) {
				log.warn("Rejected experience creation due to overlapping with: {}", exp);
				throw new IllegalArgumentException("Experience period overlaps with an existing record.");
			}
		}

		ExperienceEntity experience = ExperienceEntity.builder().companyName(request.getCompanyName())
				.role(request.getRole()).startDate(start).endDate(request.isCurrent() ? null : end)
				.current(request.isCurrent()).description(request.getDescription()).skills(request.getSkills()).build();

		experienceRepository.save(experience);
		log.info("Experience created successfully for company: {}", request.getCompanyName());
	}

	@Override
	@Transactional
	public void updateExperience(Long id, ExperienceRequest request) {
		ExperienceEntity existing = experienceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Experience not found with ID: " + id));

		validateDates(request.getStartDate(), request.getEndDate(), request.isCurrent());

		if (!isExperienceChanged(existing, request)) {
			log.info("No changes detected for experience with ID: {}", id);
			throw new IllegalArgumentException("No changes detected in experience details.");
		}

		LocalDate newStart = request.getStartDate();
		LocalDate newEnd = request.isCurrent() ? LocalDate.now() : request.getEndDate();

		List<ExperienceEntity> all = experienceRepository.findAll().stream().filter(e -> !e.getId().equals(id))
				.collect(Collectors.toList());

		for (ExperienceEntity exp : all) {
			LocalDate existingStart = exp.getStartDate();
			LocalDate existingEnd = exp.isCurrent() ? LocalDate.now() : exp.getEndDate();

			boolean overlaps = !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
			if (overlaps) {
				log.warn("Rejected experience update due to overlap with ID: {}", exp.getId());
				throw new IllegalArgumentException("Experience period overlaps with another record.");
			}
		}

		existing.setCompanyName(request.getCompanyName());
		existing.setRole(request.getRole());
		existing.setStartDate(request.getStartDate());
		existing.setEndDate(request.getEndDate());
		existing.setCurrent(request.isCurrent());
		existing.setDescription(request.getDescription());
		existing.setSkills(request.getSkills());

		experienceRepository.save(existing);
		log.info("Updated experience for company: {}", request.getCompanyName());
	}

	private boolean isExperienceChanged(ExperienceEntity existing, ExperienceRequest request) {
		if (!Objects.equals(existing.getCompanyName(), request.getCompanyName()))
			return true;
		if (!Objects.equals(existing.getRole(), request.getRole()))
			return true;
		if (!Objects.equals(existing.getStartDate(), request.getStartDate()))
			return true;
		if (!Objects.equals(existing.getEndDate(), request.getEndDate()))
			return true;
		if (existing.isCurrent() != request.isCurrent())
			return true;
		if (!Objects.equals(existing.getDescription(), request.getDescription()))
			return true;

		List<String> existingSkills = existing.getSkills() != null ? existing.getSkills() : List.of();
		List<String> requestSkills = request.getSkills() != null ? request.getSkills() : List.of();

		Set<String> existingSet = new HashSet<>(existingSkills);
		Set<String> requestSet = new HashSet<>(requestSkills);

		return !existingSet.equals(requestSet);
	}

	@Override
	public void deleteExperience(Long id) {
		if (!experienceRepository.existsById(id)) {
			throw new ResourceNotFoundException("Experience not found with ID: " + id);
		}
		experienceRepository.deleteById(id);
		log.info("Deleted experience with ID: {}", id);
	}

	@Override
	public List<ExperienceResponse> getAllExperiences() {
		List<ExperienceEntity> experiences = experienceRepository.findAll();
		if (experiences.isEmpty()) {
			throw new ResourceNotFoundException("No experience records found.");
		}

		return experiences.stream()
				.map(e -> ExperienceResponse.builder().id(e.getId()).companyName(e.getCompanyName()).role(e.getRole())
						.startDate(e.getStartDate()).endDate(e.getEndDate()).current(e.isCurrent())
						.description(e.getDescription()).skills(e.getSkills()).build())
				.collect(Collectors.toList());
	}

	private void validateDates(LocalDate startDate, LocalDate endDate, boolean isCurrent) {
		LocalDate today = LocalDate.now();

		if (startDate.isAfter(today)) {
			throw new IllegalArgumentException("Start date cannot be in the future.");
		}

		if (isCurrent) {
			if (endDate != null) {
				throw new IllegalArgumentException("End date must be null for current experience.");
			}
		} else {
			if (endDate == null) {
				throw new IllegalArgumentException("End date is required for past experience.");
			}
			if (endDate.isBefore(startDate)) {
				throw new IllegalArgumentException("End date must be after start date.");
			}
			if (endDate.isAfter(today)) {
				throw new IllegalArgumentException("End date cannot be in the future.");
			}
		}
	}
}
