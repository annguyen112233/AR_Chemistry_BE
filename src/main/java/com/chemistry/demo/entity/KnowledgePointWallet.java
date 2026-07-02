package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(
        name = "knowledge_point_wallets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_kp_wallet_user_id",
                        columnNames = "user_id"
                )
        }
)
public class KnowledgePointWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "balance", nullable = false)
    private Long balance = 0L;

    @Column(name = "total_earned", nullable = false)
    private Long totalEarned = 0L;

    @Column(name = "total_spent", nullable = false)
    private Long totalSpent = 0L;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void earn(long amount) {
        if (amount <= 0) return;

        this.balance += amount;
        this.totalEarned += amount;
    }

    public void spend(long amount) {
        if (amount <= 0) return;

        if (this.balance < amount) {
            throw new IllegalStateException("Not enough Knowledge Point");
        }

        this.balance -= amount;
        this.totalSpent += amount;
    }
}