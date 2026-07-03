package com.chemistry.demo.dto.response.reaction;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageOwnershipResponse {
    private boolean owned;
    private String accessType;
    private Instant startAt;
    private Instant expiredAt;
    private long remainingDays;
    private String message;
}