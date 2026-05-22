package com.chemistry.demo.services.redis.Impl;

import com.chemistry.demo.entity.PackageFeature;
import com.chemistry.demo.entity.Subscriptions;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.FeatureCode;
import com.chemistry.demo.repository.PackageFeatureRepository;
import com.chemistry.demo.repository.SubscriptionRepository;
import com.chemistry.demo.services.redis.RedisFeatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisFeatureServiceImpl
        implements RedisFeatureService {

    private final RedisTemplate<String, String> redisTemplate;

    private final SubscriptionRepository
            subscriptionRepository;

    private final PackageFeatureRepository
            packageFeatureRepository;

    private String buildKey(User user) {
        return "user_features:" + user.getCognitoSub();
    }

    @Override
    public void cacheUserFeatures(User user) {

        Subscriptions subscription =
                subscriptionRepository
                        .findByUserAndActiveTrue(user)
                        .orElse(null);

        if(subscription == null) {
            return;
        }

        List<PackageFeature> packageFeatures =
                packageFeatureRepository
                        .findByPackages(
                                subscription.getPackageEntity()
                        );

        String key = buildKey(user);

        redisTemplate.delete(key);

        for(PackageFeature pf : packageFeatures) {

            redisTemplate.opsForSet().add(
                    key,
                    pf.getFeatures()
                            .getCode()
                            .name()
            );
        }
    }

    @Override
    public boolean hasFeature(
            User user,
            FeatureCode featureCode
    ) {

        String key = buildKey(user);

        Boolean exists =
                redisTemplate.opsForSet()
                        .isMember(
                                key,
                                featureCode.name()
                        );
//          REDIS HIT
        if(Boolean.TRUE.equals(exists)) {
            return true;
        }
//         REDIS MISS
        Subscriptions subscription =
                subscriptionRepository
                        .findByUserAndActiveTrue(user)
                        .orElse(null);
        if(subscription == null) {
            return false;
        }
//         CHECK EXPIRED
        if(subscription.getEndDate()
                .isBefore(Instant.now())) {

            subscription.setActive(false);

            subscriptionRepository.save(subscription);

            evictUserFeatures(user);

            return false;
        }
//         LOAD FEATURES FROM DB
        List<PackageFeature> packageFeatures =
                packageFeatureRepository
                        .findByPackages(
                                subscription.getPackageEntity()
                        );
//         REBUILD REDIS CACHE
        redisTemplate.delete(key);
        for(PackageFeature pf : packageFeatures) {
            redisTemplate.opsForSet().add(
                    key,
                    pf.getFeatures()
                            .getCode()
                            .name()
            );
        }
        redisTemplate.expire(
                key,
                Duration.ofHours(6)
        );
//         CHECK AGAIN AFTER CACHE REBUILD
        Boolean afterReload =
                redisTemplate.opsForSet()
                        .isMember(
                                key,
                                featureCode.name()
                        );

        return Boolean.TRUE.equals(afterReload);
    }

    @Override
    public void evictUserFeatures(User user) {

        redisTemplate.delete(
                buildKey(user)
        );
    }
}