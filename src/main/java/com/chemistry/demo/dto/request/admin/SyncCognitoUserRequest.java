package com.chemistry.demo.dto.request.admin;

import com.chemistry.demo.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncCognitoUserRequest {
    @Email
    @NotBlank
    private String email;

    private RoleName roleName;
}
