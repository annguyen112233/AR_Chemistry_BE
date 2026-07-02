package com.chemistry.demo.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "ar_scan_reward_logs",
        indexes = {
                @Index(
                        name = "idx_ar_scan_reward_user_date",
                        columnList = "user_id, reward_date"
                )
        }
)
public class ArScanRewardLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "reward_date", nullable = false)
    private LocalDate rewardDate;

    @Column(name = "kp_rewarded", nullable = false)
    private Long kpRewarded;

    @Column(name = "rewarded", nullable = false)
    private Boolean rewarded;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public static ArScanRewardLog create(
            String userId,
            String referenceId,
            LocalDate rewardDate,
            long kpRewarded,
            boolean rewarded
    ) {
        ArScanRewardLog log = new ArScanRewardLog();
        log.userId = userId;
        log.referenceId = referenceId;
        log.rewardDate = rewardDate;
        log.kpRewarded = kpRewarded;
        log.rewarded = rewarded;
        return log;
    }
}