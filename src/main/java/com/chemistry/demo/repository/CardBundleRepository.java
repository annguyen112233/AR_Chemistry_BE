package com.chemistry.demo.repository;

import com.chemistry.demo.entity.CardBundle;
import com.chemistry.demo.entity.ChemicalCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBundleRepository extends JpaRepository<CardBundle, String> {
    Page<CardBundle> findByActiveTrueAndPurchasableTrue(Pageable pageable);

    Page<CardBundle> findByPurchasable(Boolean purchasable, Pageable pageable);
}
