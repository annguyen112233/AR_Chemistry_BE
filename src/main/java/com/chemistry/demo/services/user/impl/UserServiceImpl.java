package com.chemistry.demo.services.user.impl;

import com.chemistry.demo.dto.request.profile.UpdateProfileRequest;
import com.chemistry.demo.dto.response.profile.UpdateProfileResponse;
import com.chemistry.demo.dto.response.user.UserResponse;
import com.chemistry.demo.dto.response.profile.UserProfileResponse;
import com.chemistry.demo.entity.KnowledgePointWallet;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.mapper.UserProfileMapper;
import com.chemistry.demo.repository.KnowledgePointWalletRepository;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.aws.CognitoService;
import com.chemistry.demo.services.user.UserService;
import com.chemistry.demo.utils.SecurityUtils;
import com.chemistry.demo.utils.UserSecurityCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserSecurityCacheService userSecurityCacheService;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final SecurityUtils securityUtils;
    private final CognitoService cognitoService;
    private final KnowledgePointWalletRepository knowledgePointWalletRepository;

    @Override
    @Transactional
    public UserResponse syncUser(
            String email,
            String cognitoUsername,
            String cognitoSub,
            String fullName,
            String avatarUrl
    ) {
        AtomicBoolean isNewUser = new AtomicBoolean(false);

        Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Role STUDENT not found"));

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> {
                    isNewUser.set(true);
                    return User.builder()
                            .cognitoSub(cognitoSub)
                            .email(email)
                            .fullName(fullName)
                            .avatarUrl(avatarUrl)
                            .roles(new java.util.HashSet<>(java.util.List.of(studentRole)))
                            .build();
                });

        // Backfill thông tin từ nhà cung cấp (Google) nếu user chưa có,
        // nhưng KHÔNG đè lên avatar/tên người dùng đã tự cập nhật.
        if (isBlank(user.getAvatarUrl()) && !isBlank(avatarUrl)) {
            user.setAvatarUrl(avatarUrl);
        }
        if (isBlank(user.getFullName()) && !isBlank(fullName)) {
            user.setFullName(fullName);
        }

        User savedUser = userRepository.save(user);

        String walletUserId = savedUser.getCognitoSub();

        knowledgePointWalletRepository.findByUserId(walletUserId)
                .orElseGet(() -> knowledgePointWalletRepository.save(
                        KnowledgePointWallet.builder()
                                .userId(walletUserId)
                                .balance(30000L)
                                .totalEarned(30000L)
                                .totalSpent(0L)
                                .build()
                ));

        userSecurityCacheService.evictUserSecurity(cognitoSub);

        if (isNewUser.get()) {
            // Hardening từ nhánh check-ar: Cognito lỗi tạm thời không được làm
            // hỏng cả lần sync — user đã lưu DB, gán group có thể thử lại sau.
            try {
                cognitoService.addUserToGroup(
                        cognitoUsername,
                        RoleName.ROLE_STUDENT.name()
                );
            } catch (RuntimeException exception) {
                log.warn(
                        "User {} was saved locally but could not be added to Cognito group {}",
                        cognitoSub,
                        RoleName.ROLE_STUDENT.name(),
                        exception
                );
            }
        }

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();

        user.setPhoneNumber(request.getPhoneNumber());
        user.setFullName(request.getFullName());

        userRepository.save(user);

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

    @Override
    public UserProfileResponse getUserProfile() {
            User user = securityUtils.getCurrentUserCognitoSub();
            return userProfileMapper.toUserProfileResponse(user);
    }

    @Override
    public String updateAvatar(String avatarUrl) {
        User user = securityUtils.getCurrentUserCognitoSub();
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return "Avatar updated successfully";
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
