package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ChemicalSubstance;
import com.chemistry.demo.enums.ChemicalGroup;
import com.chemistry.demo.enums.ChemicalSubstanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ChemicalSubstanceRepository extends JpaRepository<ChemicalSubstance, String> {

    Optional<ChemicalSubstance> findByFormula(String formula);

    boolean existsByFormula(String formula);

    Page<ChemicalSubstance> findByActiveTrue(Pageable pageable);

    Page<ChemicalSubstance> findByIncludedInFullKitTrueAndActiveTrue(Pageable pageable);

    Page<ChemicalSubstance> findByChemicalGroupAndActiveTrue(
            ChemicalGroup chemicalGroup,
            Pageable pageable
    );

    Page<ChemicalSubstance> findByTypeAndActiveTrue(
            ChemicalSubstanceType type,
            Pageable pageable
    );

    // Giữ List cho logic nội bộ, ví dụ tạo Full Kit từ tất cả chất includedInFullKit=true
    List<ChemicalSubstance> findAllByIncludedInFullKitTrueAndActiveTrue();

    Page<ChemicalSubstance> findByActiveTrueAndIncludedInFullKitTrue(
            Pageable pageable
    );
    long countByActiveTrueAndIncludedInFullKitTrue();

}
