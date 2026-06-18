package com.chemistry.demo.config.seeder;

import com.chemistry.demo.aspect.NoLogging;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.enums.PermissionName;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.services.user.PermissionService;
import com.chemistry.demo.services.user.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@NoLogging
@Component
@RequiredArgsConstructor
public class RoleSeeder implements DataSeeder {

    private final RoleService roleService;
    private final PermissionService permissionService;

    @Override
    public void seed() {
        // ADMIN
        Role adminRole = roleService.createRole(RoleName.ROLE_ADMIN);
        adminRole.setPermissions(Set.of(
                // USER
                permissionService.getPermission(PermissionName.MANAGE_USERS),
                permissionService.getPermission(PermissionName.VIEW_USERS),
                permissionService.getPermission(PermissionName.CREATE_USER),
                permissionService.getPermission(PermissionName.UPDATE_USER),
                permissionService.getPermission(PermissionName.DELETE_USER),
                permissionService.getPermission(PermissionName.BLOCK_USER),
                permissionService.getPermission(PermissionName.UNBLOCK_USER),
                permissionService.getPermission(PermissionName.ASSIGN_ROLE),
                permissionService.getPermission(PermissionName.APPROVE_TEACHER),

                // CHEMICAL
                permissionService.getPermission(PermissionName.CREATE_CHEMICAL),
                permissionService.getPermission(PermissionName.VIEW_CHEMICAL),
                permissionService.getPermission(PermissionName.UPDATE_CHEMICAL),
                permissionService.getPermission(PermissionName.DELETE_CHEMICAL),
                permissionService.getPermission(PermissionName.IMPORT_CHEMICAL),
                permissionService.getPermission(PermissionName.EXPORT_CHEMICAL),
                permissionService.getPermission(PermissionName.MANAGE_CHEMICAL_STOCK),

                // REACTION
                permissionService.getPermission(PermissionName.CREATE_REACTION),
                permissionService.getPermission(PermissionName.VIEW_REACTION),
                permissionService.getPermission(PermissionName.UPDATE_REACTION),
                permissionService.getPermission(PermissionName.DELETE_REACTION),
                permissionService.getPermission(PermissionName.VERIFY_REACTION),
                permissionService.getPermission(PermissionName.APPROVE_REACTION),

                // AR
                permissionService.getPermission(PermissionName.SCAN_AR),
                permissionService.getPermission(PermissionName.UPLOAD_AR_MARKER),
                permissionService.getPermission(PermissionName.MANAGE_AR_CONTENT),
                permissionService.getPermission(PermissionName.DELETE_AR_CONTENT),

                // EXPERIMENT
                permissionService.getPermission(PermissionName.CREATE_EXPERIMENT),
                permissionService.getPermission(PermissionName.VIEW_EXPERIMENT),
                permissionService.getPermission(PermissionName.UPDATE_EXPERIMENT),
                permissionService.getPermission(PermissionName.DELETE_EXPERIMENT),
                permissionService.getPermission(PermissionName.MANAGE_LAB),

                // COURSE
                permissionService.getPermission(PermissionName.CREATE_COURSE),
                permissionService.getPermission(PermissionName.UPDATE_COURSE),
                permissionService.getPermission(PermissionName.DELETE_COURSE),
                permissionService.getPermission(PermissionName.VIEW_COURSE),
                permissionService.getPermission(PermissionName.JOIN_COURSE),
                permissionService.getPermission(PermissionName.ASSIGN_STUDENT),

                // REPORT
                permissionService.getPermission(PermissionName.VIEW_REPORTS),
                permissionService.getPermission(PermissionName.EXPORT_REPORTS),
                permissionService.getPermission(PermissionName.VIEW_STATISTICS),
                permissionService.getPermission(PermissionName.UPLOAD_FILE),

                // SAFETY
                permissionService.getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                permissionService.getPermission(PermissionName.MANAGE_SAFETY_GUIDE),
                permissionService.getPermission(PermissionName.REPORT_INCIDENT),
                permissionService.getPermission(PermissionName.VIEW_INCIDENT_REPORTS)
                )
        );

        // STAFF
        Role staffRole = roleService.createRole(RoleName.ROLE_STAFF);
        staffRole.setPermissions(Set.of(
                permissionService.getPermission(PermissionName.CREATE_CHEMICAL),
                permissionService.getPermission(PermissionName.VIEW_CHEMICAL),
                permissionService.getPermission(PermissionName.UPDATE_CHEMICAL),
                permissionService.getPermission(PermissionName.IMPORT_CHEMICAL),
                permissionService.getPermission(PermissionName.EXPORT_CHEMICAL),
                permissionService.getPermission(PermissionName.MANAGE_CHEMICAL_STOCK),
                permissionService.getPermission(PermissionName.CREATE_REACTION),
                permissionService.getPermission(PermissionName.VIEW_REACTION),
                permissionService.getPermission(PermissionName.UPDATE_REACTION),
                permissionService.getPermission(PermissionName.VERIFY_REACTION),
                permissionService.getPermission(PermissionName.APPROVE_REACTION),
                permissionService.getPermission(PermissionName.SCAN_AR),
                permissionService.getPermission(PermissionName.UPLOAD_AR_MARKER),
                permissionService.getPermission(PermissionName.MANAGE_AR_CONTENT),
                permissionService.getPermission(PermissionName.VIEW_EXPERIMENT),
                permissionService.getPermission(PermissionName.MANAGE_LAB),
                permissionService.getPermission(PermissionName.VIEW_REPORTS),
                permissionService.getPermission(PermissionName.EXPORT_REPORTS),
                permissionService.getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                permissionService.getPermission(PermissionName.REPORT_INCIDENT),
                permissionService.getPermission(PermissionName.VIEW_INCIDENT_REPORTS)));


        // STUDENT
        Role studentRole = roleService.createRole(RoleName.ROLE_STUDENT);
        studentRole.setPermissions(Set.of(
                permissionService.getPermission(PermissionName.VIEW_CHEMICAL),
                permissionService.getPermission(PermissionName.VIEW_REACTION),
                permissionService.getPermission(PermissionName.SCAN_AR),
                permissionService.getPermission(PermissionName.VIEW_EXPERIMENT),
                permissionService.getPermission(PermissionName.VIEW_COURSE),
                permissionService.getPermission(PermissionName.JOIN_COURSE),
                permissionService.getPermission(PermissionName.VIEW_SAFETY_GUIDE),
                permissionService.getPermission(PermissionName.REPORT_INCIDENT)));

        roleService.save(adminRole);
        roleService.save(staffRole);
        roleService.save(studentRole);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
