package com.chemistry.demo.dto.request.admin;

import com.chemistry.demo.enums.RoleName;
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
public class AssignRolesRequest {
    @NotEmpty
    private Set<RoleName> roleNames;
}
