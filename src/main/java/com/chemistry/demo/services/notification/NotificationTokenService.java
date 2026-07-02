package com.chemistry.demo.services.notification;

import com.chemistry.demo.dto.request.notification.RegisterNotificationTokenRequest;

public interface NotificationTokenService {
    void registerToken(RegisterNotificationTokenRequest request);

    void deactivateCurrentDeviceToken(String fcmToken);
}
