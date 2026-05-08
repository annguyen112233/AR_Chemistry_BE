package com.chemistry.demo.config;


import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.repository.PermissionRepository;
import com.chemistry.demo.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public void run(String... args) {

        Permission createChemical =
                createPermission("CREATE_CHEMICAL");

        Permission deleteChemical =
                createPermission("DELETE_CHEMICAL");

        Permission manageUsers =
                createPermission("MANAGE_USERS");

        Role adminRole = createRole("ROLE_ADMIN");
        adminRole.setPermissions(Set.of(
                createChemical,
                deleteChemical,
                manageUsers
        ));

        Role userRole = createRole("ROLE_USER");

        roleRepository.save(adminRole);
        roleRepository.save(userRole);
    }

    private Permission createPermission(String name) {
        return permissionRepository
                .findByName(name)
                .orElseGet(() ->
                        permissionRepository.save(
                                Permission.builder()
                                        .name(name)
                                        .build()
                        ));
    }

    private Role createRole(String name) {
        return roleRepository
                .findByName(name)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name(name)
                                        .build()
                        ));
    }
}