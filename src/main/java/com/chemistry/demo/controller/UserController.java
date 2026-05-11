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
import com.chemistry.demo.untils.UserSecurityService;
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
    private final UserSecurityService userSecurityService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {

        String email = jwt.getClaim("email");
        String cognitoSub = jwt.getSubject();

        userService.syncUser(email, cognitoSub);

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("successfully")
                .data(
                        userSecurityService
                                .getUserSecurity(cognitoSub)
                )
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UpdateProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UpdateProfileResponse>builder()
                .success(true)
                .message("successfully")
                .data(userService.updateProfile(request))
                .build();

    }


}
