package com.chemistry.demo.services.notification.Impl;

import com.chemistry.demo.dto.request.notification.RegisterNotificationTokenRequest;
import com.chemistry.demo.entity.NotificationToken;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.repository.NotificationTokenRepository;
import com.chemistry.demo.services.notification.NotificationTokenService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationTokenServiceImpl implements NotificationTokenService {
    private final NotificationTokenRepository notificationTokenRepository;
    private final SecurityUtils securityUtils;
    @Override
    public void registerToken(RegisterNotificationTokenRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();

        NotificationToken token = notificationTokenRepository
                .findByFcmToken(request.getFcmToken())
                .orElseGet(NotificationToken::new);


        token.setFcmToken(request.getFcmToken());
        token.setUserId(user.getCognitoSub());
        token.setPlatform(request.getPlatform());
        token.setDeviceName(request.getDeviceName());
        token.setActive(true);

        notificationTokenRepository.save(token);
    }

    @Override
    @Transactional
    public void deactivateCurrentDeviceToken(String fcmToken) {
        User user = securityUtils.getCurrentUserCognitoSub();

        notificationTokenRepository
                .findByUserIdAndFcmToken(user.getCognitoSub(), fcmToken)
                .ifPresent(token -> {
                    token.setActive(false);
                    notificationTokenRepository.save(token);
                });
    }
}
