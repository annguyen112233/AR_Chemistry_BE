package com.chemistry.demo.entity;

import com.chemistry.demo.enums.PaymentItemType;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentItemType itemType;


    @ManyToOne
    private Packages packageEntity;

    private BigDecimal amount;

    private String proofImageUrl;
    private Instant createdAt;

    @ManyToOne
    private ChemicalCard chemicalCard;

    @ManyToOne
    private CardBundle cardBundle;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    private User approvedBy;

    private Instant approvedAt;

}
