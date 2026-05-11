package com.chemistry.demo.controller;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.RejectTeacherRequest;
import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PutMapping("/select-role")
    public ApiResponse<String> updateRole(@RequestBody SelectRoleRequest request) {
        return ApiResponse.success(
                roleService.selectRole(
                        request
                )
        );
    }

    @PutMapping("/{cognitoSub}/approve")
    public ApiResponse<String> approve(
            @PathVariable String cognitoSub
    ) {
        return ApiResponse.success(
                roleService.approveTeacher(cognitoSub)
        );
    }

    @PutMapping("/{cognitoSub}/reject")
    public ApiResponse<String> reject(
            @PathVariable String cognitoSub,
            @RequestBody RejectTeacherRequest reason
    ) {
        return ApiResponse.success(
                roleService.rejectTeacher(
                        cognitoSub,
                        reason
                )
        );
    }
}
