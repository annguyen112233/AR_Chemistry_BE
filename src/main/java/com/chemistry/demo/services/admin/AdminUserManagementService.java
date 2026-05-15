package com.chemistry.demo.services.admin;

import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.AdminUsersResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserManagementService {
    PageResponse<AdminUsersResponse> getUsersForAdmin(Pageable pageable);
    String createUser();
}
