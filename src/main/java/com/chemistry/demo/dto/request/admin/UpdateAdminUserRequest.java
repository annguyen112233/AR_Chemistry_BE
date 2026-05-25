package com.chemistry.demo.dto.request.admin;

import com.chemistry.demo.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminUserRequest {
    private String fullName;

    private String phoneNumber;

    private UserStatus status;
}
