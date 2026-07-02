package com.chemistry.demo.config.scheduler;

import com.chemistry.demo.entity.NotificationToken;
import com.chemistry.demo.repository.ArScanRewardLogRepository;
import com.chemistry.demo.repository.NotificationTokenRepository;
import com.chemistry.demo.services.notification.Impl.FcmSenderService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private final NotificationTokenRepository notificationTokenRepository;
    private final ArScanRewardLogRepository arScanRewardLogRepository;
    private final FcmSenderService fcmSenderService;

    // TEST: chạy mỗi phút
    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendDailyArScanReminder() {
        LocalDate today = LocalDate.now(VN_ZONE);

        List<NotificationToken> activeTokens =
                notificationTokenRepository.findByActiveTrue();

        int sentCount = 0;
        int failedCount = 0;
        int skippedCount = 0;

        Set<String> remindedUsers = new HashSet<>();

        for (NotificationToken token : activeTokens) {
            String userId = token.getUserId();

            if (userId == null || userId.isBlank()) {
                skippedCount++;
                continue;
            }

            // Mỗi user chỉ nhận 1 thông báo dù có nhiều thiết bị.
            if (remindedUsers.contains(userId)) {
                skippedCount++;
                continue;
            }

            boolean alreadyRewardedToday =
                    arScanRewardLogRepository.existsByUserIdAndRewardDateAndRewardedTrue(
                            userId,
                            today
                    );

            if (alreadyRewardedToday) {
                skippedCount++;
                continue;
            }

            try {
                fcmSenderService.sendToToken(
                        token.getFcmToken(),
                        "AR Chemistry",
                        "Hôm nay bạn chưa quét AR! Vào quét phản ứng để nhận Knowledge Point nhé.",
                        "DAILY_AR_SCAN_REMINDER",
                        "AR_SCAN"
                );

                remindedUsers.add(userId);
                sentCount++;

            } catch (FirebaseMessagingException e) {
                failedCount++;

                if (e.getMessagingErrorCode() != null
                        && "UNREGISTERED".equals(e.getMessagingErrorCode().name())) {
                    token.setActive(false);
                    notificationTokenRepository.save(token);
                }

                log.warn(
                        "Send daily AR reminder failed. tokenId={}, userId={}, error={}",
                        token.getId(),
                        userId,
                        e.getMessage()
                );
            }
        }

        log.info(
                "Daily AR scan reminder finished. sent={}, failed={}, skipped={}",
                sentCount,
                failedCount,
                skippedCount
        );
    }
}