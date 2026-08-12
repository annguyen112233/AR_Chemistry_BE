package com.chemistry.demo.repository;

import com.chemistry.demo.entity.SingleCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

public interface SingleCardRepository extends JpaRepository<SingleCard, String> {
    Page<SingleCard> findByActiveTrue(Pageable pageable);

    Optional<SingleCard> findByCode(String code);

    boolean existsByCode(String code);
    @Query("""
    SELECT sc
    FROM SingleCard sc
    WHERE sc.active = true
      AND EXISTS (
          SELECT cc.id
          FROM ChemicalCard cc
          WHERE cc.substance.id = sc.substance.id
            AND cc.active = true
      )
""")
    Page<SingleCard> findActiveSingleCardsWithActiveChemicalCard(Pageable pageable);

}
