package com.chemistry.demo.controller.admin;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.PageResponse;
import com.chemistry.demo.dto.response.AdminUsersResponse;
import com.chemistry.demo.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
