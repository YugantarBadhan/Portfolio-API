package com.yugantar.portfolio.serviceimpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.yugantar.portfolio.dto.EducationRequest;
import com.yugantar.portfolio.dto.EducationResponse;
import com.yugantar.portfolio.entity.EducationEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.EducationRepository;
import com.yugantar.portfolio.service.EducationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EducationServiceImpl implements EducationService {

	private final EducationRepository educationRepository;

	@Override
	public void createEducation(EducationRequest request) {
		validate(request);

		boolean duplicate = !educationRepository.findByStartDateAndEndDate(request.getStartDate(), request.getEndDate())
				.isEmpty();
		if (duplicate) {
			throw new IllegalArgumentException("Education entry already exists for this period.");
		}

		EducationEntity education = mapToEntity(request);
		educationRepository.save(education);
		log.info("Education entry created for: {}", request.getDegree());
	}

	@Override
	public void updateEducation(Long id, EducationRequest request) {
		EducationEntity existing = educationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Education not found with ID: " + id));

		validate(request);

		// Check for duplicate excluding the current record
		List<EducationEntity> duplicates = educationRepository
				.findByStartDateAndEndDate(request.getStartDate(), request.getEndDate()).stream()
				.filter(e -> !e.getId().equals(id)) // exclude the current record
				.collect(Collectors.toList());

		if (!duplicates.isEmpty()) {
			throw new IllegalArgumentException("Another education entry already exists for this period.");
		}

		EducationEntity updated = mapToEntity(request);
		updated.setId(id);

		if (!isChanged(existing, updated)) {
			log.info("No update required. Education details for ID {} are unchanged.", id);
			throw new IllegalArgumentException("No changes detected in education details.");
		}

		educationRepository.save(updated);
		log.info("Updated education record with ID: {}", id);
	}

	@Override
	public void deleteEducation(Long id) {
		if (!educationRepository.existsById(id)) {
			throw new ResourceNotFoundException("Education not found with ID: " + id);
		}
		educationRepository.deleteById(id);
		log.info("Deleted education with ID: {}", id);
	}

	@Override
	public List<EducationResponse> getAllEducations() {
		List<EducationEntity> all = educationRepository.findAll();
		if (all.isEmpty()) {
			throw new ResourceNotFoundException("No education records found.");
		}

		return all.stream()
				.map(e -> EducationResponse.builder().id(e.getId()).degree(e.getDegree()).field(e.getField())
						.university(e.getUniversity()).institute(e.getInstitute()).location(e.getLocation())
						.startDate(e.getStartDate()).endDate(e.getEndDate()).currentStudying(e.isCurrentStudying())
						.grade(e.getGrade()).educationType(e.getEducationType()).description(e.getDescription())
						.build())
				.collect(Collectors.toList());
	}

	private void validate(EducationRequest req) {
		if (req.isCurrentStudying() && req.getEndDate() != null) {
			throw new IllegalArgumentException("End date should be null if currently studying is true.");
		}
		if (!req.isCurrentStudying() && req.getEndDate() == null) {
			throw new IllegalArgumentException("End date is required if not currently studying.");
		}
		if (!req.isCurrentStudying() && req.getStartDate().compareTo(req.getEndDate()) > 0) {
			throw new IllegalArgumentException("Start date must be before end date.");
		}
	}

	private boolean isChanged(EducationEntity old, EducationEntity newEdu) {
		return !Objects.equals(old, newEdu);
	}

	private EducationEntity mapToEntity(EducationRequest req) {
		return EducationEntity.builder().degree(req.getDegree()).field(req.getField()).university(req.getUniversity())
				.institute(req.getInstitute()).location(req.getLocation()).startDate(req.getStartDate())
				.endDate(req.getEndDate()).currentStudying(req.isCurrentStudying()).grade(req.getGrade())
				.educationType(req.getEducationType()).description(req.getDescription()).build();
	}
}
