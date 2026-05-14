package com.chemistry.demo.config.seeder;

import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.enums.RoleName;
import com.chemistry.demo.enums.UserStatus;
import com.chemistry.demo.repository.RoleRepository;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AccountSeeder implements DataSeeder {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.user_pool-id}")
    private String userPoolId;

    @Override
    public void seed() {

        String email = "admin@gmail.com";

        // check tồn tại chưa
        if (userService.existsByEmail(email)) {
            return;
        }

        // tạo user trên cognito
        AdminCreateUserRequest createRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .temporaryPassword("Admin123!")
                .userAttributes(
                        AttributeType.builder()
                                .name("email")
                                .value(email)
                                .build(),
                        AttributeType.builder()
                                .name("email_verified")
                                .value("true")
                                .build()
                )
                .messageAction(MessageActionType.SUPPRESS) // không gửi mail
                .build();

        AdminCreateUserResponse response =
                cognitoClient.adminCreateUser(createRequest);
        cognitoClient.adminSetUserPassword(
                AdminSetUserPasswordRequest.builder()
                        .userPoolId(userPoolId)
                        .username(email)
                        .password("13579Messi@")
                        .permanent(true)
                        .build()
        );


        // lấy sub của cognito
        String cognitoSub = response.user()
                .attributes()
                .stream()
                .filter(attr -> attr.name().equals("sub"))
                .findFirst()
                .map(AttributeType::value)
                .orElse(null);

        Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                .orElseThrow();

        User admin = User.builder()
                .email(email)
                .fullName("Admin User")
                .cognitoSub(cognitoSub)
                .status(UserStatus.ACTIVE)
                .roles(new HashSet<>(List.of(adminRole)))
                .build();

        userService.save(admin);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
