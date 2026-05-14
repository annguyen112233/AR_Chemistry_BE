package com.chemistry.demo.utils;

import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.security.UserAuthoritiesProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserAuthoritiesProvider {
        private final UserRepository userRepository;
        private final UserMapper userMapper;

        @Cacheable(value = "user-security", key = "#cognitoSub")
        public UserResponse getUserSecurity(String cognitoSub) {
                log.info("Loading user security for: {}", cognitoSub);
                User user = userRepository.findByCognitoSub(cognitoSub)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                return userMapper.toUserResponse(user);
        }

        @Override
        @Cacheable(value = "user-authorities", key = "#cognitoSub")
        public Collection<? extends GrantedAuthority> getAuthorities(String cognitoSub) {
                log.info("Fetching authorities from DB for user: {}", cognitoSub);
                
                return userRepository.findByCognitoSub(cognitoSub)
                        .map(user -> {
                                Set<SimpleGrantedAuthority> authorities = new HashSet<>();
                                for (Role role : user.getRoles()) {
                                        authorities.add(new SimpleGrantedAuthority(role.getRoleName().name()));
                                        for (Permission permission : role.getPermissions()) {
                                                authorities.add(new SimpleGrantedAuthority(permission.getName().name()));
                                        }
                                }
                                return authorities;
                        })
                        .orElse(Set.of());
        }
}
