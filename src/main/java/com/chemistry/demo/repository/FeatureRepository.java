package com.chemistry.demo.repository;

import com.chemistry.demo.entity.Feature;
import com.chemistry.demo.enums.FeatureCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeatureRepository
        extends JpaRepository<Feature, Long> {

    Optional<Feature> findByCode(
            FeatureCode code
    );
}
