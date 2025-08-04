package com.yugantar.portfolio.serviceimpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yugantar.portfolio.dto.AwardRequest;
import com.yugantar.portfolio.dto.AwardResponse;
import com.yugantar.portfolio.entity.AwardEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.AwardRepository;
import com.yugantar.portfolio.service.AwardService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwardServiceImpl implements AwardService {

	@Autowired
	private AwardRepository awardRepo;

	@Override
	public AwardResponse createAward(AwardRequest request) {
		log.info("Creating new award: {}", request.getAwardName());
		AwardEntity entity = AwardEntity.builder().awardName(request.getAwardName())
				.description(request.getDescription()).awardCompanyName(request.getAwardCompanyName())
				.awardLink(request.getAwardLink()).awardYear(request.getAwardYear()).build();
		return mapToResponse(awardRepo.save(entity));
	}

	@Override
	public AwardResponse updateAward(Long id, AwardRequest request) {
		log.info("Updating award with ID: {}", id);
		AwardEntity existing = awardRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Award not found with id: " + id));

		boolean isSame = existing.getAwardName().equalsIgnoreCase(request.getAwardName())
				&& Objects.equals(existing.getDescription(), request.getDescription())
				&& Objects.equals(existing.getAwardCompanyName(), request.getAwardCompanyName())
				&& Objects.equals(existing.getAwardLink(), request.getAwardLink())
				&& Objects.equals(existing.getAwardYear(), request.getAwardYear());

		if (isSame) {
			log.warn("No change detected for award update id: {}", id);
			throw new IllegalArgumentException("No changes detected in the request.");
		}

		existing.setAwardName(request.getAwardName());
		existing.setDescription(request.getDescription());
		existing.setAwardCompanyName(request.getAwardCompanyName());
		existing.setAwardLink(request.getAwardLink());
		existing.setAwardYear(request.getAwardYear());

		return mapToResponse(awardRepo.save(existing));
	}

	@Override
	public void deleteAward(Long id) {
		log.info("Deleting award with ID: {}", id);
		AwardEntity entity = awardRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Award not found with id: " + id));
		awardRepo.delete(entity);
	}

	@Override
	public List<AwardResponse> getAllAwards() {
		log.info("Fetching all awards");
		List<AwardEntity> list = awardRepo.findAll();
		if (list.isEmpty()) {
			throw new ResourceNotFoundException("No awards available to fetch.");
		}
		return list.stream().map(this::mapToResponse).collect(Collectors.toList());
	}

	private AwardResponse mapToResponse(AwardEntity entity) {
		return AwardResponse.builder().id(entity.getId()).awardName(entity.getAwardName())
				.description(entity.getDescription()).awardCompanyName(entity.getAwardCompanyName())
				.awardLink(entity.getAwardLink()).awardYear(entity.getAwardYear()).build();
	}
}