package com.chemistry.demo.services.user.impl;

import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.user.UserService;
import com.chemistry.demo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public UserResponse syncUser(String email,String cognitoSub) {

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

    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {
        // Tối ưu: Lấy trực tiếp User từ SecurityUtils (chỉ tốn 1 lần query DB)
        User user = securityUtils.getCurrentUserCognitoSub();

        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setFullName(request.getFullName());

        userRepository.save(user);
        log.info("Profile updated for user: {}", user.getEmail());

        return userMapper.toUpdateProfileResponse(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsUserByEmail((email));
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

}
