package com.chemistry.demo.untils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityCacheService {

    private final CacheManager cacheManager;

    private static final String CACHE_NAME = "user-security";

    public void evictUserSecurity(String cognitoSub) {

        Cache cache = cacheManager.getCache(CACHE_NAME);

        if (cache != null) {
            cache.evict(cognitoSub);
        }
    }
}
