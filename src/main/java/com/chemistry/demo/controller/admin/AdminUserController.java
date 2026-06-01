package com.chemistry.demo.controller.admin;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.admin.AssignRolesRequest;
import com.chemistry.demo.dto.request.admin.CreateAdminUserRequest;
import com.chemistry.demo.dto.request.admin.SyncCognitoUserRequest;
import com.chemistry.demo.dto.request.admin.UpdateAdminUserRequest;
import com.chemistry.demo.dto.request.admin.UpdateUserStatusRequest;
import com.chemistry.demo.dto.response.AdminUserDetailResponse;
import com.chemistry.demo.dto.response.AdminUsersResponse;
import com.chemistry.demo.services.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminService adminService;

    @GetMapping
    public ApiResponse<PageResponse<AdminUsersResponse>> getAllUsers(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC

            ) Pageable pageable
    ) {

        return ApiResponse.<PageResponse<AdminUsersResponse>>ok()
                .data(adminService.userManagement().getUsersForAdmin(pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminUserDetailResponse> getUserDetail(@PathVariable String id) {
        return ApiResponse.<AdminUserDetailResponse>ok()
                .data(adminService.userManagement().getUserDetail(id))
                .build();
    }

    @PostMapping
    public ApiResponse<AdminUserDetailResponse> createUser(
            @Valid @RequestBody CreateAdminUserRequest request
    ) {
        return ApiResponse.<AdminUserDetailResponse>created()
                .data(adminService.userManagement().createUser(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminUserDetailResponse> updateUser(
            @PathVariable String id,
            @RequestBody UpdateAdminUserRequest request
    ) {
        return ApiResponse.<AdminUserDetailResponse>ok()
                .data(adminService.userManagement().updateUser(id, request))
                .build();
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<AdminUserDetailResponse> updateUserStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ApiResponse.<AdminUserDetailResponse>ok()
                .data(adminService.userManagement().updateUserStatus(id, request))
                .build();
    }

    @PatchMapping("/{id}/roles")
    public ApiResponse<AdminUserDetailResponse> assignRoles(
            @PathVariable String id,
            @Valid @RequestBody AssignRolesRequest request
    ) {
        return ApiResponse.<AdminUserDetailResponse>ok()
                .data(adminService.userManagement().assignRoles(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> softDeleteUser(@PathVariable String id) {
        return ApiResponse.<String>ok()
                .data(adminService.userManagement().softDeleteUser(id))
                .build();
    }

    @PostMapping("/{id}/reset-password")
    public ApiResponse<String> resetPassword(@PathVariable String id) {
        return ApiResponse.<String>ok()
                .data(adminService.userManagement().resetPassword(id))
                .build();
    }

    @PostMapping("/sync-cognito")
    public ApiResponse<AdminUserDetailResponse> syncCognitoUser(
            @Valid @RequestBody SyncCognitoUserRequest request
    ) {
        return ApiResponse.<AdminUserDetailResponse>ok()
                .data(adminService.userManagement().syncCognitoUser(request))
                .build();
    }
}
