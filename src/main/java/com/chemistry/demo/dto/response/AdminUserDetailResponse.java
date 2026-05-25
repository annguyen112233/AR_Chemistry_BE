package com.chemistry.demo.dto.response;

import com.chemistry.demo.dto.response.role.RoleUsersResponse;
import com.chemistry.demo.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDetailResponse {
    private String cognitoSub;

    private String email;

    private String fullName;

    private String avatarUrl;

    private String phoneNumber;

    private UserStatus status;

    private Set<RoleUsersResponse> roles;
}
