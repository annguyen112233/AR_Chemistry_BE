package com.chemistry.demo.dto.response.payment;

import com.chemistry.demo.entity.Packages;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private User user;
    private Packages packageEntity;
    private BigDecimal amount;
    private String proofImageUrl;
    private PaymentStatus status;
}
