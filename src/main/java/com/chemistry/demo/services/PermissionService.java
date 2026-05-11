package com.chemistry.demo.services;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.PermissionName;
import com.chemistry.demo.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission getPermission(PermissionName name) {
        return permissionRepository.findByName(name)
                .orElseThrow();
    }

    public Optional<Permission> findByName(PermissionName name) {
        return permissionRepository.findByName(name);
    }

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }
}
