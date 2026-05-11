package com.chemistry.demo.services;

import com.chemistry.demo.dto.request.RejectTeacherRequest;
import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.enums.VerificationStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.untils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final SecurityUtils securityUtils;

    //dong bo thong tin user tu cognito vao database
    public UserResponse syncUser(String email, String cognitoSub) {

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .cognitoSub(cognitoSub)
                            .email(email)
                            .build();
                    return userRepository.save(newUser);
                });
        return userMapper.toUserResponse(user);
    }


    //cap nhat thong tin user
    public UpdateProfileResponse updateProfile(
            UpdateProfileRequest request
    ) {

        String cognitoSub = securityUtils.getCurrentUserCognitoSub();

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        user.setPhoneNumber(request.getPhoneNumber());

        user.setAvatarUrl(request.getAvatarUrl());

        user.setFullName(request.getFullName());

        userRepository.save(user);

        return userMapper.toUpdateProfileResponse(user);
    }
}
