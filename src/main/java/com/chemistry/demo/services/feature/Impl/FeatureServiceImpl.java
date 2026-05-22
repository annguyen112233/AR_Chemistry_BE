package com.chemistry.demo.services.feature.Impl;

import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.FeatureCode;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.SubscriptionErrorCode;
import com.chemistry.demo.services.feature.FeatureService;
import com.chemistry.demo.services.redis.RedisFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl
        implements FeatureService {

    private final RedisFeatureService
            redisFeatureService;

    @Override
    public boolean hasFeature(
            User user,
            FeatureCode featureCode
    ) {

        return redisFeatureService.hasFeature(
                user,
                featureCode
        );
    }

    @Override
    public void checkFeature(
            User user,
            FeatureCode featureCode
    ) {

        if (!hasFeature(user, featureCode)) {

            throw new AppException(
                    SubscriptionErrorCode
                            .FEATURE_NOT_AVAILABLE
            );
        }
    }
}