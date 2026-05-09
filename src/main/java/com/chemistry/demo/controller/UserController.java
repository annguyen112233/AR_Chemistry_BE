package com.chemistry.demo.controller;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.RejectTeacherRequest;
import com.chemistry.demo.dto.request.SelectRoleRequest;
import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        String email = jwt.getClaim("email");

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("successfully")
                .data(userService.syncUser(cognitoSub, email))
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UpdateProfileResponse> updateProfile(@AuthenticationPrincipal Jwt jwt
    , @RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UpdateProfileResponse>builder()
                .success(true)
                .message("successfully")
                .data(userService.updateProfile(jwt.getSubject(), request))
                .build();

    }

    @PutMapping("/role")
    public ApiResponse<String> updateRole(@AuthenticationPrincipal Jwt jwt
            , @RequestBody SelectRoleRequest request) {

        return ApiResponse.success(
                userService.selectRole(
                        jwt.getSubject(),
                        request
                )
        );
    }

    @PutMapping("/{cognitoSub}/approve")
    public ApiResponse<String> approve(
            @PathVariable String cognitoSub
    ) {

        return ApiResponse.success(
                userService.approveTeacher(cognitoSub)
        );
    }

    @PutMapping("/{cognitoSub}/reject")
    public ApiResponse<String> reject(
            @PathVariable String cognitoSub,
            @RequestBody RejectTeacherRequest reason
    ) {

        return ApiResponse.success(
                userService.rejectTeacher(
                        cognitoSub,
                        reason
                )
        );
    }
}
