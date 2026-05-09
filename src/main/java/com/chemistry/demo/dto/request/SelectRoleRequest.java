package com.chemistry.demo.dto.request;

import com.chemistry.demo.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SelectRoleRequest {
    @NotNull
    private RoleName role;
}
