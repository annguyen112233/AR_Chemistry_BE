package com.chemistry.demo.entity;

import com.chemistry.demo.enums.KnowledgePointTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "knowledge_point_transactions")
@Builder
@AllArgsConstructor
public class KnowledgePointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Earn: số dương
     * Spend: số âm
     */
    @Column(name = "amount", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private KnowledgePointTransactionType type;

    @Column(name = "description")
    private String description;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "balance_before")
    private Long balanceBefore;

    @Column(name = "balance_after")
    private Long balanceAfter;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public static KnowledgePointTransaction earn(
            String userId,
            long amount,
            KnowledgePointTransactionType type,
            String description,
            String referenceId,
            long balanceBefore,
            long balanceAfter
    ) {
        KnowledgePointTransaction tx = new KnowledgePointTransaction();
        tx.userId = userId;
        tx.amount = Math.abs(amount);
        tx.balanceBefore = balanceBefore;
        tx.balanceAfter = balanceAfter;
        tx.type = type;
        tx.description = description;
        tx.referenceId = referenceId;
        return tx;
    }

    public static KnowledgePointTransaction spend(
            String userId,
            long amount,
            KnowledgePointTransactionType type,
            String description,
            String referenceId,
            long balanceBefore,
            long balanceAfter
    ) {
        KnowledgePointTransaction tx = new KnowledgePointTransaction();
        tx.userId = userId;
        tx.amount = -Math.abs(amount);
        tx.type = type;
        tx.description = description;
        tx.referenceId = referenceId;
        tx.balanceBefore = balanceBefore;
        tx.balanceAfter = balanceAfter;
        return tx;
    }
}