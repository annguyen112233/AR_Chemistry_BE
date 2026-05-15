package com.chemistry.demo.dto.response;

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
    private Set<RoleResponse> roles;
}
