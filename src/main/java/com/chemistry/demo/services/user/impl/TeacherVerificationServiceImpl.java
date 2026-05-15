package com.chemistry.demo.services.user.impl;

import com.chemistry.demo.dto.request.role.RejectTeacherRequest;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.enums.VerificationStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.aws.CognitoService;
import com.chemistry.demo.services.user.TeacherVerificationService;
import com.chemistry.demo.utils.SecurityUtils;
import com.chemistry.demo.utils.UserSecurityCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TeacherVerificationServiceImpl implements TeacherVerificationService {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final CognitoService cognitoService;
    private final UserSecurityCacheService cacheService;

    @Override
    @PreAuthorize("hasAuthority('APPROVE_TEACHER')")
    @Transactional
    public String approveTeacher(String cognitoSub) {
        return processReview(cognitoSub, VerificationStatus.APPROVED, user -> {
            boolean isTeacher = user.getRoles().stream()
                    .anyMatch(role -> role.getRoleName() == RoleName.ROLE_TEACHER);

            if (!isTeacher) {
                throw new AppException(ErrorCode.INVALID_ROLE);
            }
            if (user.getStatus() == UserStatus.ACTIVE) {
                throw new AppException(ErrorCode.TEACHER_ALREADY_APPROVED);
            }

            cognitoService.addUserToGroup(user.getEmail(), RoleName.ROLE_TEACHER.name());
            user.setStatus(UserStatus.ACTIVE);
        });
    }

    @Override
    @PreAuthorize("hasAuthority('APPROVE_TEACHER')")
    @Transactional
    public String rejectTeacher(String cognitoSub, RejectTeacherRequest reason) {
        return processReview(cognitoSub, VerificationStatus.REJECTED, user -> {
            user.setStatus(UserStatus.REJECTED);
            user.getTeacherVerifications().forEach(v -> v.setRejectionReason(String.valueOf(reason)));
        });
    }

    private String processReview(String cognitoSub, VerificationStatus targetStatus, Consumer<User> specificLogic) {
        User approver = securityUtils.getCurrentUserCognitoSub();

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        specificLogic.accept(user);

        Instant now = Instant.now();
        user.getTeacherVerifications().forEach(verification -> {
            verification.setStatus(targetStatus);
            verification.setReviewedBy(approver);
            verification.setReviewedAt(now);
        });

        userRepository.save(user);
        cacheService.evictUserSecurity(cognitoSub);

        return targetStatus == VerificationStatus.APPROVED
                ? "Teacher approved successfully"
                : "Teacher rejected successfully";
    }
}
