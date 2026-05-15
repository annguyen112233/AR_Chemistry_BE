package com.chemistry.demo.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityCacheService {

    private final CacheManager cacheManager;

    private static final String USER_SECURITY_CACHE = "user-security";
    private static final String USER_AUTHORITIES_CACHE = "user-authorities";

    public void evictUserSecurity(String cognitoSub) {

        Cache userSecurityCache = cacheManager.getCache(USER_SECURITY_CACHE);

        if (userSecurityCache != null) {
            userSecurityCache.evict(cognitoSub);
        }

        Cache userAuthoritiesCache = cacheManager.getCache(USER_AUTHORITIES_CACHE);

        if (userAuthoritiesCache != null) {
            userAuthoritiesCache.evict(cognitoSub);
        }
    }
}
