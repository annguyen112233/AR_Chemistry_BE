package com.chemistry.demo.dto.response.KitActivationCode;

import com.chemistry.demo.enums.ActivationCodeStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivationCodeResponse {

    private String id;

    private String code;

    private String kitId;

    private String kitCode;

    private String kitName;

    private ActivationCodeStatus status;

    private String usedByUserId;

    private Instant usedAt;

    private Instant expiresAt;

    private Boolean active;

    private String note;
}
