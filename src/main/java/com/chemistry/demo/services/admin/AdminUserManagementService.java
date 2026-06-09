package com.chemistry.demo.services.admin;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.request.admin.AssignRolesRequest;
import com.chemistry.demo.dto.request.admin.CreateAdminUserRequest;
import com.chemistry.demo.dto.request.admin.SyncCognitoUserRequest;
import com.chemistry.demo.dto.request.admin.UpdateAdminUserRequest;
import com.chemistry.demo.dto.request.admin.UpdateUserStatusRequest;
import com.chemistry.demo.dto.response.user.AdminUserDetailResponse;
import com.chemistry.demo.dto.response.user.AdminUsersResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserManagementService {
    PageResponse<AdminUsersResponse> getUsersForAdmin(Pageable pageable);
    AdminUserDetailResponse getUserDetail(String id);
    AdminUserDetailResponse createUser(CreateAdminUserRequest request);
    AdminUserDetailResponse updateUser(String id, UpdateAdminUserRequest request);
    AdminUserDetailResponse updateUserStatus(String id, UpdateUserStatusRequest request);
    AdminUserDetailResponse assignRoles(String id, AssignRolesRequest request);
    String softDeleteUser(String id);
    String resetPassword(String id);
    AdminUserDetailResponse syncCognitoUser(SyncCognitoUserRequest request);
}
