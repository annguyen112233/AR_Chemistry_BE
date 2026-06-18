package com.chemistry.demo.controller.user;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.request.profile.AvatarRequest;
import com.chemistry.demo.dto.request.profile.UpdateProfileRequest;
import com.chemistry.demo.dto.response.profile.UpdateProfileResponse;
import com.chemistry.demo.dto.response.reaction.ArAccessResponse;
import com.chemistry.demo.dto.response.user.UserResponse;
import com.chemistry.demo.dto.response.profile.UserProfileResponse;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.services.reaction.ArAccessService;
import com.chemistry.demo.services.user.UserService;
import com.chemistry.demo.utils.UserSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final ArAccessService arAccessService;


    @GetMapping("/me")
    public ApiResponse<UserResponse> me(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaim("email");
        String cognitoSub = jwt.getSubject();
        String cognitoUsername =
                jwt.getClaimAsString(
                        "cognito:username"
                );

        userService.syncUser(email,cognitoUsername, cognitoSub);

        return ApiResponse.<UserResponse>ok()
                .data(userSecurityService.getUserSecurity(cognitoSub))
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UpdateProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ApiResponse.<UpdateProfileResponse>ok()
                .data(userService.updateProfile(request))
                .build();
    }
    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile() {
        return ApiResponse.<UserProfileResponse>ok()
                .data(userService.getUserProfile())
                .build();
    }
    @PutMapping("/avatar")
    public ApiResponse<String> updateAvatar( @RequestBody AvatarRequest avatarUrl) {
        userService.updateAvatar(avatarUrl.getAvatarUrl());
        return ApiResponse.<String>ok()
                .data("Avatar updated successfully")
                .build();
    }

    @GetMapping("/ar-access")
    public ApiResponse<ArAccessResponse> getMyArAccess() {

        return ApiResponse.<ArAccessResponse>ok()
                .data(arAccessService.getMyArAccess())
                .build();
    }


}
