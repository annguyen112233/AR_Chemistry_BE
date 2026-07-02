package com.chemistry.demo.dto.response.knowledgePoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ArScanRewardResponse {

    private boolean rewarded;
    private long kpEarned;
    private long todayRewardedCount;
    private long dailyLimit;
    private long currentBalance;
    private String message;
}