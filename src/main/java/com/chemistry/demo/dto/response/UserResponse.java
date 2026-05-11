package com.chemistry.demo.dto.response;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.RoleName;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements java.io.Serializable{
    private Long id;

    private String email;

    private String status;
    private Set<RoleResponse> roles;
}
