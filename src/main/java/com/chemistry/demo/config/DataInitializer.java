package com.chemistry.demo.config;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.enums.PermissionName;
import com.chemistry.demo.enums.RoleName;
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

        // create all permissions
        for (PermissionName permissionName : PermissionName.values()) {

            permissionRepository.findByName(permissionName)
                    .orElseGet(() ->
                            permissionRepository.save(
                                    Permission.builder()
                                            .name(permissionName)
                                            .build()
                            )
                    );
        }

        // ADMIN
        Role adminRole = createRole(RoleName.ROLE_ADMIN);

        adminRole.setPermissions(Set.of(

                // USER
                getPermission(PermissionName.MANAGE_USERS),
                getPermission(PermissionName.VIEW_USERS),
                getPermission(PermissionName.CREATE_USER),
                getPermission(PermissionName.UPDATE_USER),
                getPermission(PermissionName.DELETE_USER),
                getPermission(PermissionName.BLOCK_USER),
                getPermission(PermissionName.UNBLOCK_USER),
                getPermission(PermissionName.ASSIGN_ROLE),
                getPermission(PermissionName.APPROVE_TEACHER),

                // CHEMICAL
                getPermission(PermissionName.CREATE_CHEMICAL),
                getPermission(PermissionName.VIEW_CHEMICAL),
                getPermission(PermissionName.UPDATE_CHEMICAL),
                getPermission(PermissionName.DELETE_CHEMICAL),
                getPermission(PermissionName.IMPORT_CHEMICAL),
                getPermission(PermissionName.EXPORT_CHEMICAL),
                getPermission(PermissionName.MANAGE_CHEMICAL_STOCK),

                // REACTION
                getPermission(PermissionName.CREATE_REACTION),
                getPermission(PermissionName.VIEW_REACTION),
                getPermission(PermissionName.UPDATE_REACTION),
                getPermission(PermissionName.DELETE_REACTION),
                getPermission(PermissionName.VERIFY_REACTION),
                getPermission(PermissionName.APPROVE_REACTION),

                // AR
                getPermission(PermissionName.SCAN_AR),
                getPermission(PermissionName.UPLOAD_AR_MARKER),
                getPermission(PermissionName.MANAGE_AR_CONTENT),
                getPermission(PermissionName.DELETE_AR_CONTENT),

                // EXPERIMENT
                getPermission(PermissionName.CREATE_EXPERIMENT),
                getPermission(PermissionName.VIEW_EXPERIMENT),
                getPermission(PermissionName.UPDATE_EXPERIMENT),
                getPermission(PermissionName.DELETE_EXPERIMENT),
                getPermission(PermissionName.MANAGE_LAB),

                // COURSE
                getPermission(PermissionName.CREATE_COURSE),
                getPermission(PermissionName.UPDATE_COURSE),
                getPermission(PermissionName.DELETE_COURSE),
                getPermission(PermissionName.VIEW_COURSE),
                getPermission(PermissionName.JOIN_COURSE),
                getPermission(PermissionName.ASSIGN_STUDENT),

                // REPORT
                getPermission(PermissionName.VIEW_REPORTS),
                getPermission(PermissionName.EXPORT_REPORTS),
                getPermission(PermissionName.VIEW_STATISTICS),

                // SAFETY
                getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                getPermission(PermissionName.MANAGE_SAFETY_GUIDE),
                getPermission(PermissionName.REPORT_INCIDENT),
                getPermission(PermissionName.VIEW_INCIDENT_REPORTS)
        ));

        // STAFF
        Role staffRole = createRole(RoleName.ROLE_STAFF);

        staffRole.setPermissions(Set.of(

                getPermission(PermissionName.APPROVE_TEACHER),

                getPermission(PermissionName.CREATE_CHEMICAL),
                getPermission(PermissionName.VIEW_CHEMICAL),
                getPermission(PermissionName.UPDATE_CHEMICAL),
                getPermission(PermissionName.IMPORT_CHEMICAL),
                getPermission(PermissionName.EXPORT_CHEMICAL),
                getPermission(PermissionName.MANAGE_CHEMICAL_STOCK),

                getPermission(PermissionName.CREATE_REACTION),
                getPermission(PermissionName.VIEW_REACTION),
                getPermission(PermissionName.UPDATE_REACTION),
                getPermission(PermissionName.VERIFY_REACTION),
                getPermission(PermissionName.APPROVE_REACTION),

                getPermission(PermissionName.SCAN_AR),
                getPermission(PermissionName.UPLOAD_AR_MARKER),
                getPermission(PermissionName.MANAGE_AR_CONTENT),

                getPermission(PermissionName.VIEW_EXPERIMENT),
                getPermission(PermissionName.MANAGE_LAB),

                getPermission(PermissionName.VIEW_REPORTS),
                getPermission(PermissionName.EXPORT_REPORTS),

                getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                getPermission(PermissionName.REPORT_INCIDENT),
                getPermission(PermissionName.VIEW_INCIDENT_REPORTS)
        ));

        // TEACHER
        Role teacherRole = createRole(RoleName.ROLE_TEACHER);

        teacherRole.setPermissions(Set.of(

                getPermission(PermissionName.VIEW_CHEMICAL),
                getPermission(PermissionName.VIEW_REACTION),

                getPermission(PermissionName.SCAN_AR),
                getPermission(PermissionName.UPLOAD_AR_MARKER),

                getPermission(PermissionName.CREATE_EXPERIMENT),
                getPermission(PermissionName.VIEW_EXPERIMENT),
                getPermission(PermissionName.UPDATE_EXPERIMENT),

                getPermission(PermissionName.CREATE_COURSE),
                getPermission(PermissionName.UPDATE_COURSE),
                getPermission(PermissionName.VIEW_COURSE),
                getPermission(PermissionName.ASSIGN_STUDENT),

                getPermission(PermissionName.VIEW_REPORTS),
                getPermission(PermissionName.VIEW_STATISTICS),

                getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                getPermission(PermissionName.REPORT_INCIDENT)
        ));

        // STUDENT
        Role studentRole = createRole(RoleName.ROLE_STUDENT);

        studentRole.setPermissions(Set.of(

                getPermission(PermissionName.VIEW_CHEMICAL),
                getPermission(PermissionName.VIEW_REACTION),

                getPermission(PermissionName.SCAN_AR),

                getPermission(PermissionName.VIEW_EXPERIMENT),

                getPermission(PermissionName.VIEW_COURSE),
                getPermission(PermissionName.JOIN_COURSE),

                getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                getPermission(PermissionName.REPORT_INCIDENT)
        ));

        roleRepository.save(adminRole);
        roleRepository.save(staffRole);
        roleRepository.save(teacherRole);
        roleRepository.save(studentRole);
    }

    private Permission getPermission(PermissionName name) {
        return permissionRepository.findByName(name)
                .orElseThrow();
    }

    private Role createRole(RoleName name) {
        return roleRepository
                .findByRoleName(name)
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .roleName(name)
                                        .build()
                        ));
    }
}