package com.chemistry.demo.dto.response;

import com.chemistry.demo.dto.response.role.RoleUsersResponse;
import com.chemistry.demo.enums.UserStatus;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUsersResponse {


    private String cognitoSub;

    private String email;

    private String fullName;

    private String avatarUrl;

    private String phoneNumber;

    private UserStatus status;

    private Set<RoleUsersResponse> roles;

}
