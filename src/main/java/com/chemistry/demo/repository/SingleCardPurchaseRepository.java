package com.chemistry.demo.repository;

import com.chemistry.demo.entity.SingleCardPurchase;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SingleCardPurchaseRepository extends JpaRepository<SingleCardPurchase, String> {
    List<SingleCardPurchase> findByUserOrderByPurchasedAtDesc(User user);

    Page<SingleCardPurchase> findByUserAndStatusOrderByPurchasedAtDesc(
            User user,
            PurchaseStatus status,
            Pageable pageable
    );


    Optional<SingleCardPurchase> findByIdAndUserAndStatus(String referenceId, User user, PurchaseStatus purchaseStatus);
}
