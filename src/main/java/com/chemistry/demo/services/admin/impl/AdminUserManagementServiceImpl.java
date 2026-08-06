package com.chemistry.demo.services.admin.impl;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.admin.AssignRolesRequest;
import com.chemistry.demo.dto.request.admin.CreateAdminUserRequest;
import com.chemistry.demo.dto.request.admin.SyncCognitoUserRequest;
import com.chemistry.demo.dto.request.admin.UpdateAdminUserRequest;
import com.chemistry.demo.dto.request.admin.UpdateUserStatusRequest;
import com.chemistry.demo.dto.response.user.AdminUserDetailResponse;
import com.chemistry.demo.dto.response.user.AdminUsersResponse;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.UserErrorCode;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.admin.AdminUserManagementService;
import com.chemistry.demo.services.aws.CognitoService;
import com.chemistry.demo.utils.UserSecurityCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserManagementServiceImpl implements AdminUserManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final CognitoService cognitoService;
    private final UserSecurityCacheService cacheService;

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional(readOnly = true)
    public PageResponse<AdminUsersResponse> getUsersForAdmin(Pageable pageable) {
        Page<User> users = userRepository.findAllWithRoles(pageable);

        List<AdminUsersResponse> items = users.getContent()
                .stream()
                .map(userMapper::toAdminUsersResponse)
                .toList();

        return PageResponse.<AdminUsersResponse>builder()
                .items(items)
                .page(users.getNumber())
                .size(users.getSize())
                .totalItems(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .first(users.isFirst())
                .last(users.isLast())
                .hasNext(users.hasNext())
                .hasPrevious(users.hasPrevious())
                .build();
    }

    @Override
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Transactional
    public AdminUserDetailResponse createUser(CreateAdminUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String cognitoSub = resolveCognitoSub(request.getEmail(), request.getPassword());
        Set<Role> roles = resolveRoles(request.getRoleNames());

        User user = User.builder()
                .cognitoSub(cognitoSub)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .status(request.getStatus() != null ? request.getStatus() : UserStatus.ACTIVE)
                .roles(roles)
                .build();

        syncCognitoGroup(user.getEmail(), roles);
        syncCognitoStatus(user.getEmail(), user.getStatus());

        return userMapper.toAdminUserDetailResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(String id) {
        return userMapper.toAdminUserDetailResponse(getUserOrThrow(id));
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional
    public AdminUserDetailResponse updateUser(String id, UpdateAdminUserRequest request) {
        User user = getUserOrThrow(id);
        UserStatus originalStatus = user.getStatus();

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        if (request.getStatus() != null && request.getStatus() != originalStatus) {
            if (request.getStatus() != UserStatus.ACTIVE) {
                validateNotLastAdmin(user);
            }
            user.setStatus(request.getStatus());
            syncCognitoStatus(user.getEmail(), request.getStatus());
        }

        User savedUser = userRepository.save(user);
        cacheService.evictUserSecurity(savedUser.getCognitoSub());
        return userMapper.toAdminUserDetailResponse(savedUser);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional
    public AdminUserDetailResponse updateUserStatus(String id, UpdateUserStatusRequest request) {
        User user = getUserOrThrow(id);
        if (request.getStatus() != UserStatus.ACTIVE) {
            validateNotLastAdmin(user);
        }

        user.setStatus(request.getStatus());
        syncCognitoStatus(user.getEmail(), request.getStatus());

        User savedUser = userRepository.save(user);
        cacheService.evictUserSecurity(savedUser.getCognitoSub());
        return userMapper.toAdminUserDetailResponse(savedUser);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional
    public AdminUserDetailResponse assignRoles(String id, AssignRolesRequest request) {
        User user = getUserOrThrow(id);
        Set<Role> newRoles = resolveRoles(request.getRoleNames());
        boolean isRemovingAdmin = hasRole(user, RoleName.ROLE_ADMIN)
                && newRoles.stream().noneMatch(role -> role.getRoleName() == RoleName.ROLE_ADMIN);

        if (isRemovingAdmin) {
            validateNotLastAdmin(user);
        }

        removeStaleCognitoGroups(user, newRoles);
        user.setRoles(newRoles);
        syncCognitoGroup(user.getEmail(), newRoles);

        User savedUser = userRepository.save(user);
        cacheService.evictUserSecurity(savedUser.getCognitoSub());
        return userMapper.toAdminUserDetailResponse(savedUser);
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional
    public String softDeleteUser(String id) {
        User user = getUserOrThrow(id);
        validateNotLastAdmin(user);

        user.setStatus(UserStatus.DELETED);
        syncCognitoStatus(user.getEmail(), UserStatus.DELETED);
        userRepository.save(user);
        cacheService.evictUserSecurity(user.getCognitoSub());

        return "User deleted successfully";
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional
    public String resetPassword(String id) {
        User user = getUserOrThrow(id);
        cognitoService.resetPassword(user.getEmail());
        return "Password reset successfully";
    }

    @Override
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    @Transactional
    public AdminUserDetailResponse syncCognitoUser(SyncCognitoUserRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(userMapper::toAdminUserDetailResponse)
                .orElseGet(() -> createUserFromCognito(request));
    }

    private User getUserOrThrow(String id) {
        return userRepository.findByCognitoSub(id)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
    }

    private Role getRoleOrThrow(RoleName roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(UserErrorCode.ROLE_NOT_FOUND));
    }

    private Set<Role> resolveRoles(Set<RoleName> roleNames) {
        return roleNames.stream()
                .map(this::getRoleOrThrow)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String resolveCognitoSub(String email, String password) {
        return cognitoService.getUserSubByEmail(email)
                .orElseGet(() -> cognitoService.createUser(email, password));
    }

    private void syncCognitoGroup(String email, Set<Role> roles) {
        roles.forEach(role -> cognitoService.addUserToGroupIfNeeded(email, role.getRoleName().name()));
    }

    /**
     * Đồng bộ trạng thái sang Cognito ở dạng best-effort: DB là nguồn sự thật
     * (mọi request đều bị chặn qua check status trong UserSecurityService),
     * nên lệch dữ liệu phía Cognito không được phép làm rollback transaction —
     * trước đây user kẹt vĩnh viễn ở BLOCKED vì enableUser ném lỗi.
     */
    private void syncCognitoStatus(String email, UserStatus status) {
        try {
            if (status == UserStatus.ACTIVE) {
                cognitoService.enableUser(email);
            } else if (status == UserStatus.INACTIVE
                    || status == UserStatus.DELETED
                    || status == UserStatus.BLOCKED
                    || status == UserStatus.REJECTED) {
                cognitoService.disableUser(email);
                // Thu hồi refresh token để user bị chặn không xin được token mới.
                cognitoService.globalSignOut(email);
            }
        } catch (AppException e) {
            log.warn("Cognito status sync failed for {} (target status {}): {}",
                    email, status, e.getMessage());
        }
    }

    private void removeStaleCognitoGroups(User user, Set<Role> newRoles) {
        Set<RoleName> newRoleNames = newRoles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());

        user.getRoles().stream()
                .map(Role::getRoleName)
                .filter(roleName -> !newRoleNames.contains(roleName))
                .forEach(roleName -> cognitoService.removeUserFromGroup(user.getEmail(), roleName.name()));
    }

    private void validateNotLastAdmin(User user) {
        if (hasRole(user, RoleName.ROLE_ADMIN)
                && user.getStatus() == UserStatus.ACTIVE
                && userRepository.countByRoleNameAndStatus(RoleName.ROLE_ADMIN, UserStatus.ACTIVE) <= 1) {
            throw new AppException(UserErrorCode.CANNOT_DELETE_LAST_ADMIN);
        }
    }

    private boolean hasRole(User user, RoleName roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName() == roleName);
    }

    private AdminUserDetailResponse createUserFromCognito(SyncCognitoUserRequest request) {
        String cognitoSub = cognitoService.getUserSubByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(UserErrorCode.COGNITO_USER_NOT_FOUND));
        RoleName roleName = request.getRoleName() != null ? request.getRoleName() : RoleName.ROLE_STAFF;
        Role role = getRoleOrThrow(roleName);

        User user = User.builder()
                .cognitoSub(cognitoSub)
                .email(request.getEmail())
                .status(UserStatus.ACTIVE)
                .roles(new HashSet<>(Set.of(role)))
                .build();

        syncCognitoGroup(user.getEmail(), user.getRoles());
        User savedUser = userRepository.save(user);
        cacheService.evictUserSecurity(savedUser.getCognitoSub());
        return userMapper.toAdminUserDetailResponse(savedUser);
    }
}
