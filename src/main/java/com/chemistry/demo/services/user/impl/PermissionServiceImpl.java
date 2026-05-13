package com.chemistry.demo.services.user.impl;

import com.chemistry.demo.aspect.NoLogging;
import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.PermissionName;
import com.chemistry.demo.repository.PermissionRepository;
import com.chemistry.demo.services.user.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @NoLogging
    @Override
    public Permission getPermission(PermissionName name) {
        return permissionRepository.findByName(name)
                .orElseThrow();
    }

    @Override
    public Optional<Permission> findByName(PermissionName name) {
        return permissionRepository.findByName(name);
    }

    @NoLogging
    @Override
    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }
}
