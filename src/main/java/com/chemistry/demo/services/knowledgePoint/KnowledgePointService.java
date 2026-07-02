package com.chemistry.demo.services.knowledgePoint;

import com.chemistry.demo.dto.request.knowledgePoint.ArScanRewardRequest;
import com.chemistry.demo.dto.response.knowledgePoint.ArScanRewardResponse;
import com.chemistry.demo.dto.response.knowledgePoint.KnowledgePointWalletResponse;
import com.chemistry.demo.entity.KnowledgePointTransaction;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.KnowledgePointTransactionType;

public interface KnowledgePointService {
    KnowledgePointWalletResponse getMyWallet();
    ArScanRewardResponse rewardArScan(String referenceId);

    KnowledgePointTransaction spend(
            User user,
            long amount,
            KnowledgePointTransactionType type,
            String description,
            String referenceId
    );
}
