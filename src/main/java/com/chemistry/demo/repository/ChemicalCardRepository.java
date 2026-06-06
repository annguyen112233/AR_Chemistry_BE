package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ChemicalCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalCardRepository extends JpaRepository<ChemicalCard, String> {

    Optional<ChemicalCard> findByCardCode(String cardCode);

    Optional<ChemicalCard> findByQrPayload(String qrPayload);

    boolean existsByCardCode(String cardCode);

    boolean existsByQrPayload(String qrPayload);

    Page<ChemicalCard> findByActiveTrue(Pageable pageable);

    Page<ChemicalCard> findBySubstance_Id(String substanceId, Pageable pageable);
}
