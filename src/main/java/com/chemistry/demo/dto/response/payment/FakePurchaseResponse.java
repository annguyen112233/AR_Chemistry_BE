package com.chemistry.demo.dto.response.payment;

import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FakePurchaseResponse {

    private boolean success;

    private String message;

    private String accessType;

    private Instant startAt;

    private Instant expiredAt;

    private boolean canScanAR;
}