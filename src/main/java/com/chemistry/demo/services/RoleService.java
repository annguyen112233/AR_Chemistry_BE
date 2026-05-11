package com.chemistry.demo.services;

import com.chemistry.demo.dto.request.RejectTeacherRequest;
import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.enums.VerificationStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.untils.SecurityUtils;
import com.chemistry.demo.untils.UserSecurityCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityUtils securityUtils;
    private final CognitoIdentityProviderClient cognitoClient;
    private final UserSecurityCacheService cacheService;

    @Value("${aws.user_pool-id}")
    private String userPoolId;


    public Role createRole(RoleName name) {
        return roleRepository
                .findByRoleName(name)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .roleName(name)
                                        .build()
                        ));
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    //chon role cho user
    @Transactional
    public String selectRole(
            SelectRoleRequest request
    ) {

        String cognitoSub = securityUtils.getCurrentUserCognitoSub();


        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        if (!user.getRoles().isEmpty()) {
            throw new AppException(ErrorCode.ROLE_ALREADY_ASSIGNED);
        }

        RoleName roleName = request.getRole();

        if (roleName == RoleName.ROLE_TEACHER) {
            user.setStatus(UserStatus.PENDING);
        } else {
            user.setStatus(UserStatus.ACTIVE);

            AdminAddUserToGroupRequest groupRequest =
                    AdminAddUserToGroupRequest.builder()
                            .groupName(roleName.name())
                            .userPoolId(userPoolId)
                            .username(user.getEmail())
                            .build();

            cognitoClient.adminAddUserToGroup(groupRequest);
        }


        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() ->
                        new AppException(ErrorCode.ROLE_NOT_FOUND)
                );


        user.setRoles(new HashSet<>(List.of(role)));


        userRepository.save(user);

        cacheService.evictUserSecurity(cognitoSub);
        log.info("RETURN SELECT ROLE");

        return "Role " + roleName + " selected";
    }

    //duyet role cho user (role STAFF no duyet)
    @PreAuthorize("hasAuthority('APPROVE_TEACHER')")
    @Transactional
    public String approveTeacher(String cognitoSub) {

        User approver = securityUtils.getCurrentUser();



        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        boolean isTeacher = user.getRoles().stream()
                .anyMatch(role ->
                        role.getRoleName() == RoleName.ROLE_TEACHER
                );

        if (!isTeacher) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.TEACHER_ALREADY_APPROVED);
        }

        AdminAddUserToGroupRequest request =
                AdminAddUserToGroupRequest.builder()
                        .groupName(RoleName.ROLE_TEACHER.name())
                        .userPoolId(userPoolId)
                        .username(user.getEmail())
                        .build();

        cognitoClient.adminAddUserToGroup(request);

        user.setStatus(UserStatus.ACTIVE);

        user.getTeacherVerifications().forEach(verification -> {
            verification.setStatus(VerificationStatus.APPROVED);
            verification.setReviewedBy(approver);
            verification.setReviewedAt(Instant.now());
        });

        userRepository.save(user);

        cacheService.evictUserSecurity(cognitoSub);

        return "Teacher approved successfully";
    }

    //tu choi role cho user (role STAFF no duyet)
    @PreAuthorize("hasAuthority('APPROVE_TEACHER')")
    @Transactional
    public String rejectTeacher(
            String cognitoSub,
            RejectTeacherRequest reason
    ) {

        User approver = securityUtils.getCurrentUser();

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        user.setStatus(UserStatus.REJECTED);

        user.getTeacherVerifications().forEach(verification -> {

            verification.setStatus(VerificationStatus.REJECTED);
            verification.setReviewedBy(approver);
            verification.setReviewedAt(Instant.now());
            verification.setRejectionReason(String.valueOf(reason));
        });

        userRepository.save(user);

        return "Teacher rejected successfully";
    }
}
