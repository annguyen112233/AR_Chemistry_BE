package com.chemistry.demo.services.user.impl;

import com.chemistry.demo.aspect.NoLogging;
import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.dto.request.role.SelectRoleRequest;
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
import com.chemistry.demo.utils.SecurityUtils;
import com.chemistry.demo.utils.UserSecurityCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityUtils securityUtils;
    private final CognitoService cognitoService;
    private final UserSecurityCacheService cacheService;

    @NoLogging
    @Override
    public Role createRole(RoleName name) {
        return roleRepository.findByRoleName(name)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleName(name)
                                .build()));
    }

    @NoLogging
    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Override
    public String selectRole(com.chemistry.demo.dto.request.role.SelectRoleRequest request) {
        return "";
    }

    @Override
    public String selectRole(SelectRoleRequest request) {
        User user = securityUtils.getCurrentUserCognitoSub();

        if (!user.getRoles().isEmpty()) {
            throw new AppException(ErrorCode.ROLE_ALREADY_ASSIGNED);
        }

        RoleName roleName = request.getRole();
        applyRoleAssignmentLogic(user, roleName);

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);
        cacheService.evictUserSecurity(user.getCognitoSub());

        return "Role " + roleName + " selected successfully";
    }

    private void applyRoleAssignmentLogic(User user, RoleName roleName) {
        if (roleName == RoleName.ROLE_TEACHER) {
            user.setStatus(UserStatus.PENDING);
        } else {
            user.setStatus(UserStatus.ACTIVE);
            cognitoService.addUserToGroup(user.getEmail(), roleName.name());
        }
    }
}
