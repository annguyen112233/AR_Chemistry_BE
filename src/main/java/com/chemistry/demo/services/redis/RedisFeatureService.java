package com.chemistry.demo.services.redis;

import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.FeatureCode;

public interface RedisFeatureService {

    void cacheUserFeatures(User user);

    boolean hasFeature(
            User user,
            FeatureCode featureCode
    );

    void evictUserFeatures(User user);
}
