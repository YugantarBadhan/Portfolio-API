package com.yugantar.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yugantar.portfolio.entity.AwardEntity;

public interface AwardRepository extends JpaRepository<AwardEntity, Long> {
}