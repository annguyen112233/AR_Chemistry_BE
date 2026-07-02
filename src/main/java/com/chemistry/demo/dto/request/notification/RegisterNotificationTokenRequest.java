package com.chemistry.demo.dto.request.notification;

import com.chemistry.demo.enums.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterNotificationTokenRequest {

    @NotBlank
    private String fcmToken;

    @NotNull
    private DevicePlatform platform;

    private String deviceName;

}