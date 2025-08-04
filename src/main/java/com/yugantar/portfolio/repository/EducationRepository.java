package com.yugantar.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yugantar.portfolio.entity.EducationEntity;

public interface EducationRepository extends JpaRepository<EducationEntity, Long> {
	List<EducationEntity> findByStartDateAndEndDate(String startDate, String endDate);
}