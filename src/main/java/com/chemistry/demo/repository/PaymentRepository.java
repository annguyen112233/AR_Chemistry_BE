package com.chemistry.demo.repository;

import com.chemistry.demo.entity.PaymentRequest;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentRequest, String> {

    Page<PaymentRequest> findByStatus(PaymentStatus status, Pageable pageable);
}
