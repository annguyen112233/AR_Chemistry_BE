package com.chemistry.demo.dto.request.admin;

import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdminUserRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String fullName;

    private String phoneNumber;

    private UserStatus status;

    @NotEmpty
    private Set<RoleName> roleNames;
}
