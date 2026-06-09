package com.chemistry.demo.dto.response.role;

import com.chemistry.demo.dto.response.user.PermissionResponse;
import com.chemistry.demo.enums.RoleName;
import lombok.*;

import java.util.Set;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse implements java.io.Serializable{
    private RoleName roleName;

    private Set<PermissionResponse> permissions;
}
