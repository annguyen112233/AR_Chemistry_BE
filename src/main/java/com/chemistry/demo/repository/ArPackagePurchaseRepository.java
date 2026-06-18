package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ArPackagePurchase;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArPackagePurchaseRepository extends JpaRepository<ArPackagePurchase, String> {
    List<ArPackagePurchase> findByUserOrderByPurchasedAtDesc(User user);

    List<ArPackagePurchase> findByUserAndStatusOrderByPurchasedAtDesc(
            User user,
            PurchaseStatus status
    );

    Optional<ArPackagePurchase> findByIdAndUserAndStatus(
            String id,
            User user,
            PurchaseStatus status
    );
}
