package com.yugantar.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yugantar.portfolio.entity.SkillEntity;

public interface SkillRepository extends JpaRepository<SkillEntity, Long> {
	Optional<SkillEntity> findByNameIgnoreCase(String name);
}
