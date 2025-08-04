package com.yugantar.portfolio.service;

import java.util.List;

import com.yugantar.portfolio.dto.AwardRequest;
import com.yugantar.portfolio.dto.AwardResponse;

public interface AwardService {
	AwardResponse createAward(AwardRequest request);

	AwardResponse updateAward(Long id, AwardRequest request);

	void deleteAward(Long id);

	List<AwardResponse> getAllAwards();
}
