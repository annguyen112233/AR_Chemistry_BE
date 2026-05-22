package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Packages packageEntity;

    private BigDecimal amount;

    private String proofImageUrl;
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    private User approvedBy;

    private Instant approvedAt;

}
