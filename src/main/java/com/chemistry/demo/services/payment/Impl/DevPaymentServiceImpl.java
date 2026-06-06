package com.chemistry.demo.services.payment.Impl;

import com.chemistry.demo.dto.response.payment.FakePurchaseResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.entity.UserAccess;
import com.chemistry.demo.enums.AccessSource;
import com.chemistry.demo.enums.AccessStatus;
import com.chemistry.demo.enums.AccessType;
import com.chemistry.demo.repository.UserAccessRepository;
import com.chemistry.demo.services.payment.DevPaymentService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DevPaymentServiceImpl implements DevPaymentService {
    private final SecurityUtils securityUtils;
    private final UserAccessRepository userAccessRepository;

    @Override
    @Transactional
    public FakePurchaseResponse fakePurchaseAr30Days() {
        User user = securityUtils.getCurrentUserCognitoSub();

        Instant now = Instant.now();
        Instant expiredAt = now.plus(30, ChronoUnit.DAYS);

        UserAccess userAccess = UserAccess.builder()
                .user(user)
                .accessType(AccessType.AR_30_DAYS)
                .source(AccessSource.DEV_FAKE_PAYMENT)
                .startAt(now)
                .expiredAt(expiredAt)
                .status(AccessStatus.ACTIVE)
                .referenceId("DEV_FAKE_PAYMENT")
                .build();

        userAccessRepository.save(userAccess);

        return FakePurchaseResponse.builder()
                .success(true)
                .message("Thanh toán giả thành công. Bạn có thể quét AR trong 30 ngày.")
                .accessType(AccessType.AR_30_DAYS.name())
                .startAt(now)
                .expiredAt(expiredAt)
                .canScanAR(true)
                .build();
    }
}
