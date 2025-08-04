package com.yugantar.portfolio.service;

import java.util.List;

import com.yugantar.portfolio.dto.CertificationRequest;
import com.yugantar.portfolio.dto.CertificationResponse;

public interface CertificationService {
	void createCertification(CertificationRequest request);

	void updateCertification(Long id, CertificationRequest request);

	void deleteCertification(Long id);

	List<CertificationResponse> getAllCertifications();
}
