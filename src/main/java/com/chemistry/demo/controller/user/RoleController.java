package com.chemistry.demo.controller.user;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.services.user.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PutMapping("/select-role")
    public ApiResponse<String> updateRole(@RequestBody SelectRoleRequest request) {
        return ApiResponse.ok(
                roleService.selectRole(request));
    }
}
