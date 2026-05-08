package com.chemistry.demo.controller;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.mapper.UserMapper;
import com.chemistry.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        String cognitoSub = jwt.getSubject();
        String email = jwt.getClaim("email");

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Get current user successfully")
                .data(userService.syncUser(cognitoSub, email))
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UpdateProfileResponse> updateProfile(@AuthenticationPrincipal Jwt jwt
    , @RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UpdateProfileResponse>builder()
                .success(true)
                .message("Update profile successfully")
                .data(userService.updateProfile(jwt.getSubject(), request))
                .build();

    }


}
