package com.chemistry.demo.untils;


import com.chemistry.demo.entity.User;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        String email = Objects.requireNonNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        ).getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );
    }

    public String getCurrentUserCognitoSub() {
        String sub = Objects.requireNonNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        ).getName();

        User user = userRepository.findByCognitoSub(sub)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        return user.getCognitoSub();
    }
}
