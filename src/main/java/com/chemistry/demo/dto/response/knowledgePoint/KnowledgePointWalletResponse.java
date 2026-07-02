package com.chemistry.demo.dto.response.knowledgePoint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KnowledgePointWalletResponse {

    private Long balance;
    private Long totalEarned;
    private Long totalSpent;
}