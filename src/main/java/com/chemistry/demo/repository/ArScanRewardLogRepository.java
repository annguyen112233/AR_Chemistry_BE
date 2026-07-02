package com.chemistry.demo.repository;

import com.chemistry.demo.entity.ArScanRewardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ArScanRewardLogRepository  extends JpaRepository<ArScanRewardLog, String> {
    long countByUserIdAndRewardDateAndRewardedTrue(
            String userId,
            LocalDate rewardDate
    );

    boolean existsByUserIdAndRewardDateAndRewardedTrue(
            String userId,
            LocalDate rewardDate
    );
}
