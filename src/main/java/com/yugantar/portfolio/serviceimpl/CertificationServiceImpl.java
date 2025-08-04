package com.yugantar.portfolio.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yugantar.portfolio.dto.CertificationRequest;
import com.yugantar.portfolio.dto.CertificationResponse;
import com.yugantar.portfolio.entity.CertificationEntity;
import com.yugantar.portfolio.exception.ResourceNotFoundException;
import com.yugantar.portfolio.repository.CertificationRepository;
import com.yugantar.portfolio.service.CertificationService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CertificationServiceImpl implements CertificationService {

	@Autowired
	private CertificationRepository certificationRepo;

	@Override
	public void createCertification(CertificationRequest request) {
		CertificationEntity entity = CertificationEntity.builder().title(request.getTitle())
				.description(request.getDescription()).monthYear(request.getMonthYear())
				.certificationLink(request.getCertificationLink()).build();

		certificationRepo.save(entity);
		log.info("Created certification: {}", request.getTitle());
	}

	@Override
	public void updateCertification(Long id, CertificationRequest request) {
		CertificationEntity cert = certificationRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Certification not found with ID: " + id));

		if (cert.getTitle().equals(request.getTitle()) && cert.getDescription().equals(request.getDescription())
				&& cert.getMonthYear().equals(request.getMonthYear())
				&& cert.getCertificationLink().equals(request.getCertificationLink())) {
			log.warn("No changes detected for certification ID: {}", id);
			throw new IllegalArgumentException("No changes detected to update.");
		}

		cert.setTitle(request.getTitle());
		cert.setDescription(request.getDescription());
		cert.setMonthYear(request.getMonthYear());
		cert.setCertificationLink(request.getCertificationLink());

		certificationRepo.save(cert);
		log.info("Updated certification ID: {}", id);
	}

	@Override
	public void deleteCertification(Long id) {
		CertificationEntity cert = certificationRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Certification not found with ID: " + id));
		certificationRepo.delete(cert);
		log.info("Deleted certification ID: {}", id);
	}

	@Override
	public List<CertificationResponse> getAllCertifications() {
		List<CertificationEntity> list = certificationRepo.findAll();
		if (list.isEmpty()) {
			throw new ResourceNotFoundException("No certifications available to fetch.");
		}
		return list.stream()
				.map(cert -> CertificationResponse.builder().id(cert.getId()).title(cert.getTitle())
						.description(cert.getDescription()).monthYear(cert.getMonthYear())
						.certificationLink(cert.getCertificationLink()).build())
				.collect(Collectors.toList());
	}
}
