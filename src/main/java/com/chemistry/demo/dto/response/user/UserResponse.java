package com.chemistry.demo.dto.response.user;

import com.chemistry.demo.dto.response.role.RoleResponse;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements java.io.Serializable{
    private String email;
    private String status;
    private String fullName;
    private String avatarUrl;
    private Set<RoleResponse> roles;
}
