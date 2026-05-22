package com.chemistry.demo.dto.response.payment;

import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String email;

    private Packages packageEntity;

    private BigDecimal amount;

    private String proofImageUrl;

    private PaymentStatus status;
}
