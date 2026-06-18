package com.chemistry.demo.repository;

import com.chemistry.demo.entity.CompoundDetail;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
@Repository
public interface CompoundDetailRepository extends JpaRepository<CompoundDetail, String> {

    Optional<CompoundDetail> findBySubstance_Id(String substanceId);

    Optional<CompoundDetail> findBySubstance_Formula(String formula);

    boolean existsBySubstance_Id(String substanceId);

    boolean existsBySubstance_Formula(String formula);

    Optional<CompoundDetail> findByCasNumber(String casNumber);
}
