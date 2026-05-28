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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    @Transactional
    public void seed() {

        seedAdmin();

        seedStaff();
    }

    private void seedAdmin() {
        seedAccount(adminEmail, adminPassword, RoleName.ROLE_ADMIN, "Admin User");
    }

    private void seedStaff() {
        seedAccount(staffEmail, staffPassword, RoleName.ROLE_STAFF, "Staff User");
    }

    private void seedAccount(
            String email,
            String password,
            RoleName roleName,
            String fullName
    ) {

        if (userService.existsByEmail(email)) {
            log.info("{} user already exists, skipping seeding.", roleName.name());
            return;
        }

        try {

            Optional<String> cognitoSubOpt = cognitoService.getUserSubByEmail(email);
            String cognitoSub;

            if (cognitoSubOpt.isPresent()) {
                cognitoSub = cognitoSubOpt.get();
                log.info(
                        "User {} exists in Cognito but not DB, syncing to DB.",
                        email
                );
            } else {
                log.info("Creating {} user in Cognito...", roleName.name());
                cognitoSub = cognitoService.createUser(email, password);
            }

            cognitoService.addUserToGroupIfNeeded(
                    email,
                    roleName.name()
            );

            Role role = roleRepository
                    .findByRoleName(roleName)
                    .orElseThrow(() ->
                            new RuntimeException("Role " + roleName.name() + " not found")
                    );

            User user = User.builder()
                    .email(email)
                    .fullName(fullName)
                    .cognitoSub(cognitoSub)
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(List.of(role)))
                    .build();

            userService.save(user);

            log.info("{} user seeded successfully.", roleName.name());

        } catch (Exception e) {

            log.error(
                    "Failed to seed {} account: {}",
                    roleName.name(),
                    e.getMessage()
            );
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
