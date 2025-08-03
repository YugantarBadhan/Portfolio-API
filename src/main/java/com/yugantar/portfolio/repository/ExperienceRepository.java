package com.yugantar.portfolio.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yugantar.portfolio.entity.ExperienceEntity;

public interface ExperienceRepository extends JpaRepository<ExperienceEntity, Long> {

	@Query("SELECT e FROM ExperienceEntity e WHERE " + "(:startDate <= e.endDate AND :endDate >= e.startDate)")
	List<ExperienceEntity> findOverlappingExperiences(LocalDate startDate, LocalDate endDate);

}
