package com.chemistry.demo.services.aws.impl;

import com.chemistry.demo.services.aws.CognitoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;

import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CognitoServiceImpl implements CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.user_pool-id}")
    private String userPoolId;

    @Value("${admin.temp-password}")
    private String tempPassword;

    @Override
    public void addUserToGroup(String email, String groupName) {
        AdminAddUserToGroupRequest groupRequest = AdminAddUserToGroupRequest.builder()
                .groupName(groupName)
                .userPoolId(userPoolId)
                .username(email)
                .build();

        cognitoClient.adminAddUserToGroup(groupRequest);
    }

    @Override
    public void addUserToGroupIfNeeded(String email, String groupName) {
        String nextToken = null;

        do {
            AdminListGroupsForUserResponse groupsResponse = cognitoClient.adminListGroupsForUser(
                    AdminListGroupsForUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(email)
                            .nextToken(nextToken)
                            .build()
            );

            boolean alreadyInGroup = groupsResponse.groups().stream()
                    .anyMatch(group -> groupName.equals(group.groupName()));

            if (alreadyInGroup) {
                log.info("User {} is already in Cognito group {}, skipping.", email, groupName);
                return;
            }

            nextToken = groupsResponse.nextToken();
        } while (nextToken != null);

        addUserToGroup(email, groupName);
    }

    @Override
    public String createUser(String email, String password) {
        AdminCreateUserRequest createRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .temporaryPassword(tempPassword)
                .userAttributes(
                        AttributeType.builder().name("email").value(email).build(),
                        AttributeType.builder().name("email_verified").value("true").build()
                )
                .messageAction(MessageActionType.SUPPRESS)
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(createRequest);
        
        cognitoClient.adminSetUserPassword(AdminSetUserPasswordRequest.builder()
                .userPoolId(userPoolId)
                .username(email)
                .password(password)
                .permanent(true)
                .build());

        return response.user().attributes().stream()
                .filter(attr -> attr.name().equals("sub"))
                .findFirst()
                .map(AttributeType::value)
                .orElseThrow(() -> new RuntimeException("Failed to get sub from Cognito"));
    }

    @Override
    public Optional<String> getUserSubByEmail(String email) {
        try {
            AdminGetUserResponse response = cognitoClient.adminGetUser(
                    AdminGetUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(email)
                            .build()
            );

            return response.userAttributes().stream()
                    .filter(attribute -> "sub".equals(attribute.name()))
                    .findFirst()
                    .map(AttributeType::value);
        } catch (UserNotFoundException e) {
            return Optional.empty();
        }
    }
}
