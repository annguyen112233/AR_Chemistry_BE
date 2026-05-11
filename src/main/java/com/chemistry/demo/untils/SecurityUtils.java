package com.chemistry.demo.untils;

import com.chemistry.demo.entity.User;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

        private final UserRepository userRepository;

        /**
         * Lấy sub trực tiếp từ Token (getName() thường trả về sub trong OAuth2/Cognito)
         * Không tốn query DB.
         */
        public String getCurrentSub() {
                return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                                .map(Authentication::getName)
                                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        }

        /**
         * Lấy user hiện tại từ DB dựa trên sub trong Token
         */
        public User getCurrentUser() {
                return userRepository.findByCognitoSub(getCurrentSub())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        }
}
