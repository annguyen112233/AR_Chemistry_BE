package com.chemistry.demo.services.reaction.Impl;

import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.AccessStatus;
import com.chemistry.demo.repository.UserAccessRepository;
import com.chemistry.demo.services.reaction.ArAccessService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArAccessServiceImpl implements ArAccessService {
    private final UserAccessRepository userAccessRepository;
    private final SecurityUtils securityUtils;

    @Override
    public boolean canScanAR(User user) {
        return userAccessRepository.existsByUserAndStatusAndExpiredAtAfter(
                user,
                AccessStatus.ACTIVE,
                Instant.now()
        );
    }

    public ArAccessResponse getMyArAccess() {
        User user = securityUtils.getCurrentUserCognitoSub();

        Instant now = Instant.now();

        Optional<UserAccess> activeAccessOpt =
                userAccessRepository.findFirstByUserAndStatusAndExpiredAtAfterOrderByExpiredAtDesc(
                        user,
                        AccessStatus.ACTIVE,
                        now
                );

        if (activeAccessOpt.isEmpty()) {
            return ArAccessResponse.builder()
                    .canScanAR(false)
                    .accessType("FREE")
                    .startAt(null)
                    .expiredAt(null)
                    .remainingDays(0)
                    .message("Bạn cần kích hoạt mã kit hoặc mua gói AR 30 Days để quét AR.")
                    .build();
        }

        UserAccess access = activeAccessOpt.get();

        long remainingDays = Duration.between(now, access.getExpiredAt()).toDays();

        return ArAccessResponse.builder()
                .canScanAR(true)
                .accessType(access.getAccessType().name())
                .startAt(access.getStartAt())
                .expiredAt(access.getExpiredAt())
                .remainingDays(Math.max(remainingDays, 0))
                .message("Bạn có thể quét AR.")
                .build();
    }

}
