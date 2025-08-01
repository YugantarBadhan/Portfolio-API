package com.yugantar.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yugantar.portfolio.entity.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
	List<ProjectEntity> findAllByTitleIgnoreCase(String title);

}
