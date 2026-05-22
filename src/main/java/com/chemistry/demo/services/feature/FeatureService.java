package com.chemistry.demo.services.feature;

import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.FeatureCode;

public interface FeatureService {
    boolean hasFeature(
            User user,
            FeatureCode featureCode
    );

    void checkFeature(
            User user,
            FeatureCode featureCode
    );
}
