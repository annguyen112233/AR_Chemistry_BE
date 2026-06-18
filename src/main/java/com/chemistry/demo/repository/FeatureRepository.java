package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Features;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureRepository extends JpaRepository<Features, String> {
}
