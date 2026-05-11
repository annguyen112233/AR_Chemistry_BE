package com.chemistry.demo.services.user.impl;

import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.ErrorCode;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.aws.CognitoService;
import com.chemistry.demo.services.user.RoleService;
import com.chemistry.demo.untils.SecurityUtils;
import com.chemistry.demo.untils.UserSecurityCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityUtils securityUtils;
    private final CognitoService cognitoService;
    private final UserSecurityCacheService cacheService;

    @Override
    public Role createRole(RoleName name) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleName(name)
                                .build()));
    }

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public String selectRole(SelectRoleRequest request) {
        // 1. Lấy User chỉ bằng 1 lần query duy nhất
        User user = securityUtils.getCurrentUser();

        // 2. Kiểm tra nếu đã có role (Chống hack hoặc lỗi client)
        if (!user.getRoles().isEmpty()) {
            throw new AppException(ErrorCode.ROLE_ALREADY_ASSIGNED);
        }

        RoleName roleName = request.getRole();

        // 3. Xử lý logic trạng thái dựa trên Role
        applyRoleAssignmentLogic(user, roleName);

        // 4. Gán Role thực tế
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);

        // 5. Đồng bộ Cache
        cacheService.evictUserSecurity(user.getCognitoSub());
        log.info("Role {} assigned to user {}", roleName, user.getCognitoSub());

        return "Role " + roleName + " selected successfully";
    }

    /**
     * Tách logic xử lý trạng thái để dễ mở rộng (SRP)
     */
    private void applyRoleAssignmentLogic(User user, RoleName roleName) {
        if (roleName == RoleName.ROLE_TEACHER) {
            user.setStatus(UserStatus.PENDING);
            log.debug("Teacher role requested, status set to PENDING for user: {}", user.getEmail());
        } else {
            user.setStatus(UserStatus.ACTIVE);
            cognitoService.addUserToGroup(user.getEmail(), roleName.name());
            log.debug("Role {} assigned instantly for user: {}", roleName, user.getEmail());
        }
    }
}
