package com.chemistry.demo.untils;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSecurityService {
        private final UserRepository userRepository;
        private final UserMapper userMapper;

        @Cacheable(value = "user-security", key = "#cognitoSub")
        public UserResponse getUserSecurity(
                        String cognitoSub) {

                log.info("Loading from db");

                User user = userRepository.findByCognitoSub(cognitoSub)
                                .orElseThrow(() -> new AppException(
                                                ErrorCode.USER_NOT_FOUND));

                return userMapper.toUserResponse(user);
        }
}
