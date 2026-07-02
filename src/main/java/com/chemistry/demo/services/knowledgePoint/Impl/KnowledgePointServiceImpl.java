package com.chemistry.demo.services.knowledgePoint.Impl;

import com.chemistry.demo.dto.request.knowledgePoint.ArScanRewardRequest;
import com.chemistry.demo.dto.response.knowledgePoint.ArScanRewardResponse;
import com.chemistry.demo.dto.response.knowledgePoint.KnowledgePointWalletResponse;
import com.chemistry.demo.entity.ArScanRewardLog;
import com.chemistry.demo.entity.KnowledgePointTransaction;
import com.chemistry.demo.entity.KnowledgePointWallet;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.KnowledgePointTransactionType;
import com.chemistry.demo.repository.ArScanRewardLogRepository;
import com.chemistry.demo.repository.KnowledgePointTransactionRepository;
import com.chemistry.demo.repository.KnowledgePointWalletRepository;
import com.chemistry.demo.services.knowledgePoint.KnowledgePointService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class KnowledgePointServiceImpl implements KnowledgePointService {
    private final SecurityUtils securityUtils;
    private final KnowledgePointWalletRepository walletRepository;
    private final ArScanRewardLogRepository arScanRewardLogRepository;
    private final KnowledgePointTransactionRepository transactionRepository;

    private static final long AR_SCAN_REWARD_KP = 500L;
    private static final long AR_SCAN_DAILY_LIMIT = 5L;
    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");


    @Override
    @Transactional
    public KnowledgePointWalletResponse getMyWallet() {
        User user = securityUtils.getCurrentUserCognitoSub();

        KnowledgePointWallet wallet = getOrCreateWallet(user.getCognitoSub());

        return new KnowledgePointWalletResponse(
                wallet.getBalance(),
                wallet.getTotalEarned(),
                wallet.getTotalSpent()
        );
    }

    @Override
    @Transactional
    public ArScanRewardResponse rewardArScan(String referenceId) {
        User user = securityUtils.getCurrentUserCognitoSub();
        String userId = user.getCognitoSub();

        LocalDate today = LocalDate.now(VN_ZONE);

        KnowledgePointWallet wallet = getOrCreateWalletForUpdate(userId);

        long rewardedCountToday =
                arScanRewardLogRepository.countByUserIdAndRewardDateAndRewardedTrue(
                        userId,
                        today
                );

        if (rewardedCountToday >= AR_SCAN_DAILY_LIMIT) {
            arScanRewardLogRepository.save(
                    ArScanRewardLog.create(
                            userId,
                            referenceId,
                            today,
                            0L,
                            false
                    )
            );

            return new ArScanRewardResponse(
                    false,
                    0L,
                    rewardedCountToday,
                    AR_SCAN_DAILY_LIMIT,
                    wallet.getBalance(),
                    "Hôm nay bạn đã đạt giới hạn nhận Knowledge Point từ quét AR"
            );
        }

        long balanceBefore = wallet.getBalance() == null ? 0L : wallet.getBalance();

        wallet.earn(AR_SCAN_REWARD_KP);
        walletRepository.save(wallet);

        long balanceAfter = wallet.getBalance() == null ? 0L : wallet.getBalance();

        arScanRewardLogRepository.save(
                ArScanRewardLog.create(
                        userId,
                        referenceId,
                        today,
                        AR_SCAN_REWARD_KP,
                        true
                )
        );

        transactionRepository.save(
                KnowledgePointTransaction.earn(
                        userId,
                        AR_SCAN_REWARD_KP,
                        KnowledgePointTransactionType.EARN_AR_SCAN,
                        "Nhận Knowledge Point từ quét AR",
                        referenceId,
                        balanceBefore,
                        balanceAfter
                )
        );

        long newRewardedCount = rewardedCountToday + 1;

        return new ArScanRewardResponse(
                true,
                AR_SCAN_REWARD_KP,
                newRewardedCount,
                AR_SCAN_DAILY_LIMIT,
                wallet.getBalance(),
                "Bạn nhận được " + AR_SCAN_REWARD_KP + " Knowledge Point"
        );
    }

    @Override
    @Transactional
    public KnowledgePointTransaction spend(
            User user,
            long amount,
            KnowledgePointTransactionType type,
            String description,
            String referenceId
    ) {
        if (user == null) {
            throw new RuntimeException("User is required");
        }

        if (amount <= 0) {
            throw new RuntimeException("Knowledge Point amount must be greater than 0");
        }

        String userId = user.getCognitoSub();

        KnowledgePointWallet wallet = walletRepository
                .findByUserIdForUpdate(userId)
                .orElseThrow(() -> new RuntimeException("Knowledge Point wallet not found"));

        long balanceBefore = wallet.getBalance() == null ? 0L : wallet.getBalance();

        if (balanceBefore < amount) {
            throw new RuntimeException("Not enough Knowledge Point");
        }

        wallet.spend(amount);
        walletRepository.save(wallet);

        long balanceAfter = wallet.getBalance() == null ? 0L : wallet.getBalance();

        KnowledgePointTransaction transaction = KnowledgePointTransaction.spend(
                userId,
                amount,
                type,
                description,
                referenceId,
                balanceBefore,
                balanceAfter
        );

        return transactionRepository.save(transaction);
    }


    private KnowledgePointWallet getOrCreateWallet(String userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(
                        KnowledgePointWallet.builder()
                                .userId(userId)
                                .balance(30000L)
                                .totalEarned(30000L)
                                .totalSpent(0L)
                                .build()
                ));
    }

    private KnowledgePointWallet getOrCreateWalletForUpdate(String userId) {
        return walletRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> walletRepository.save(
                        KnowledgePointWallet.builder()
                                .userId(userId)
                                .balance(0L)
                                .totalEarned(0L)
                                .totalSpent(0L)
                                .build()
                ));
    }
}
