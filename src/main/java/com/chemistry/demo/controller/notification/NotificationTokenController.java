package com.chemistry.demo.controller.notification;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.notification.LogoutNotificationTokenRequest;
import com.chemistry.demo.dto.request.notification.RegisterNotificationTokenRequest;
import com.chemistry.demo.services.notification.NotificationTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification-tokens")
@RequiredArgsConstructor
public class NotificationTokenController {
    private final NotificationTokenService notificationTokenService;

    @PostMapping
    public ApiResponse<?> registerToken(
            @Valid @RequestBody RegisterNotificationTokenRequest request
    ) {

        notificationTokenService.registerToken(request);

        return ApiResponse.ok()
                .message("Notification token registered successfully")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<?> logoutCurrentDevice(
            @Valid @RequestBody LogoutNotificationTokenRequest request
    ) {

        notificationTokenService.deactivateCurrentDeviceToken(
                request.getFcmToken()
        );

        return ApiResponse.ok()
                .message("Notification token deactivated successfully")
                .build();
    }
}
