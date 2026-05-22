package com.chemistry.demo.config.seeder;

import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.services.aws.CognitoService;
import com.chemistry.demo.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountSeeder implements DataSeeder {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final CognitoService cognitoService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${staff.email}")
    private String staffEmail;

    @Value("${staff.password}")
    private String staffPassword;

    @Override
    public void seed() {

        seedAdmin();

        seedStaff();
    }

    private void seedAdmin() {

        if (userService.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping seeding.");
            return;
        }

        try {

            log.info("Creating admin user in Cognito...");

            String cognitoSub =
                    cognitoService.createUser(
                            adminEmail,
                            adminPassword
                    );

            cognitoService.addUserToGroup(
                    adminEmail,
                    RoleName.ROLE_ADMIN.name()
            );

            Role adminRole = roleRepository
                    .findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() ->
                            new RuntimeException("Role ADMIN not found")
                    );

            User admin = User.builder()
                    .email(adminEmail)
                    .fullName("Admin User")
                    .cognitoSub(cognitoSub)
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(List.of(adminRole)))
                    .build();

            userService.save(admin);

            log.info("Admin user seeded successfully.");

        } catch (Exception e) {

            log.error(
                    "Failed to seed admin account: {}",
                    e.getMessage()
            );
        }
    }

    private void seedStaff() {

        if (userService.existsByEmail(staffEmail)) {
            log.info("Staff user already exists, skipping seeding.");
            return;
        }

        try {

            log.info("Creating staff user in Cognito...");

            String cognitoSub =
                    cognitoService.createUser(
                            staffEmail,
                            staffPassword
                    );

            cognitoService.addUserToGroup(
                    staffEmail,
                    RoleName.ROLE_STAFF.name()
            );

            Role staffRole = roleRepository
                    .findByRoleName(RoleName.ROLE_STAFF)
                    .orElseThrow(() ->
                            new RuntimeException("Role STAFF not found")
                    );

            User staff = User.builder()
                    .email(staffEmail)
                    .fullName("Staff User")
                    .cognitoSub(cognitoSub)
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(List.of(staffRole)))
                    .build();

            userService.save(staff);

            log.info("Staff user seeded successfully.");

        } catch (Exception e) {

            log.error(
                    "Failed to seed staff account: {}",
                    e.getMessage()
            );
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}