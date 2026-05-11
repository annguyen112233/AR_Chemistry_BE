package com.chemistry.demo.services.user;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.PermissionName;

import java.util.Optional;

public interface PermissionService {
    Permission getPermission(PermissionName name);

    Optional<Permission> findByName(PermissionName name);

    Permission save(Permission permission);
}
