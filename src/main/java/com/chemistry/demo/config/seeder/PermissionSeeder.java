package com.chemistry.demo.config.seeder;

import com.chemistry.demo.aspect.NoLogging;
import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.enums.PermissionName;
import com.chemistry.demo.services.user.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@NoLogging
@Component
@RequiredArgsConstructor
public class PermissionSeeder implements DataSeeder {

    private final PermissionService permissionService;

    @Override
    public void seed() {
        for (PermissionName permissionName : PermissionName.values()) {
            permissionService.findByName(permissionName)
                    .orElseGet(() -> permissionService.save(
                            Permission.builder()
                                    .name(permissionName)
                                    .build()));
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
