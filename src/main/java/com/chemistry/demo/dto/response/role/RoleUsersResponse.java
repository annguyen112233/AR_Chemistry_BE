package com.chemistry.demo.dto.response.role;

import com.chemistry.demo.enums.RoleName;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUsersResponse {
    private RoleName roleName;
}
