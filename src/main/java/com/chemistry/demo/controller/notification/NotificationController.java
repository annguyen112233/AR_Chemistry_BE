package com.chemistry.demo.controller.notification;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.entity.NotificationToken;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.repository.NotificationTokenRepository;
import com.chemistry.demo.services.notification.Impl.FcmSenderService;
import com.chemistry.demo.utils.SecurityUtils;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationTokenRepository notificationTokenRepository;
    private final FcmSenderService fcmSenderService;
    private final SecurityUtils securityUtils;

    @PostMapping("/test/me")
    public ApiResponse<?> sendTestToMe() {

        User user = securityUtils.getCurrentUserCognitoSub();

        List<NotificationToken> tokens =
                notificationTokenRepository.findByUserIdAndActiveTrue(
                        user.getCognitoSub()
                );

        int sentCount = 0;
        int failedCount = 0;

        for (NotificationToken token : tokens) {
            try {
                fcmSenderService.sendToToken(
                        token.getFcmToken(),
                        "AR Chemistry",
                        "Hôm nay bạn chưa quét AR! Vào nhận thêm Knowledge Point nhé.",
                        "DAILY_AR_SCAN_REMINDER",
                        "AR_SCAN"
                );

                sentCount++;

            } catch (FirebaseMessagingException e) {
                failedCount++;

                if (e.getMessagingErrorCode() != null
                        && "UNREGISTERED".equals(e.getMessagingErrorCode().name())) {

                    token.setActive(false);
                    notificationTokenRepository.save(token);
                }
            }
        }

        return ApiResponse.ok()
                .message("Sent " + sentCount + " notifications, failed " + failedCount)
                .build();
    }
}