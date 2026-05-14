package com.chemistry.demo.services.user;

import com.chemistry.demo.dto.request.role.SelectRoleRequest;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.enums.RoleName;

public interface RoleService {
    Role createRole(RoleName name);

    void save(Role role);

    String selectRole(SelectRoleRequest request);

}
