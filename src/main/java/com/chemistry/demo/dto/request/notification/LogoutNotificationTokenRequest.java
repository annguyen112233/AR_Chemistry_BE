package com.chemistry.demo.dto.request.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutNotificationTokenRequest {
    @NotBlank
    private String fcmToken;
}
