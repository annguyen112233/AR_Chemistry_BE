package com.chemistry.demo.dto.response.reaction;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArAccessResponse {

    private boolean canScanAR;

    private String accessType;

    private Instant startAt;

    private Instant expiredAt;

    private long remainingDays;

    private String message;
}