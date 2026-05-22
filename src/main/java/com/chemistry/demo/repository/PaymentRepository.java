package com.chemistry.demo.repository;

import com.chemistry.demo.dto.response.payment.PaymentResponse;
import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.Payment;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByIdAndStatus(
            String id,
            PaymentStatus status
    );
    boolean existsByUserAndPackageEntityAndStatus(
            User user,
            Packages packageEntity,
            PaymentStatus status
    );

    boolean existsByUserAndStatus(
            User user,
            PaymentStatus status
    );


}
