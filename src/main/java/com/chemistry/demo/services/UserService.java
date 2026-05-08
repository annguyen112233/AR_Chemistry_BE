package com.chemistry.demo.services;

import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    //dong bo thong tin user tu cognito vao database
    public UserResponse syncUser(String cognitoSub, String email) {

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .cognitoSub(cognitoSub)
                            .email(email)
                            .status(UserStatus.ACTIVE)
                            .build();
                    return userRepository.save(newUser);
                });
        return userMapper.toUserResponse(user);
    }


    //cap nhat thong tin user
    public UpdateProfileResponse updateProfile(
            String cognitoSub,
            UpdateProfileRequest request
    ) {

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPhoneNumber(request.getPhoneNumber());

        user.setAvatarUrl(request.getAvatarUrl());

        user.setFullName(request.getFullName());

        userRepository.save(user);

        return userMapper.toUpdateProfileResponse(user);
    }
}
