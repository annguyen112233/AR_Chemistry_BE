package com.chemistry.demo.services.aws.impl;

import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.UserErrorCode;
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
        try {
            AdminAddUserToGroupRequest groupRequest = AdminAddUserToGroupRequest.builder()
                    .groupName(groupName)
                    .userPoolId(userPoolId)
                    .username(email)
                    .build();

            cognitoClient.adminAddUserToGroup(groupRequest);
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to add user {} to Cognito group {}", email, groupName, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }

    @Override
    public void addUserToGroupIfNeeded(String email, String groupName) {
        try {
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
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to check Cognito group {} for user {}", groupName, email, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }

    @Override
    public void removeUserFromGroup(String email, String groupName) {
        try {
            cognitoClient.adminRemoveUserFromGroup(AdminRemoveUserFromGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .groupName(groupName)
                    .build());
        } catch (UserNotFoundException e) {
            throw new AppException(UserErrorCode.COGNITO_USER_NOT_FOUND);
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to remove user {} from Cognito group {}", email, groupName, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }

    @Override
    public void enableUser(String email) {
        try {
            cognitoClient.adminEnableUser(AdminEnableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .build());
        } catch (UserNotFoundException e) {
            throw new AppException(UserErrorCode.COGNITO_USER_NOT_FOUND);
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to enable Cognito user {}", email, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }

    @Override
    public void disableUser(String email) {
        try {
            cognitoClient.adminDisableUser(AdminDisableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .build());
        } catch (UserNotFoundException e) {
            throw new AppException(UserErrorCode.COGNITO_USER_NOT_FOUND);
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to disable Cognito user {}", email, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }

    @Override
    public void resetPassword(String email) {
        try {
            cognitoClient.adminResetUserPassword(AdminResetUserPasswordRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .build());
        } catch (UserNotFoundException e) {
            throw new AppException(UserErrorCode.COGNITO_USER_NOT_FOUND);
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to reset password for Cognito user {}", email, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }

    @Override
    public String createUser(String email, String password) {
        try {
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
                    .orElseThrow(() -> new AppException(UserErrorCode.COGNITO_OPERATION_FAILED));
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to create Cognito user {}", email, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
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
        } catch (CognitoIdentityProviderException e) {
            log.error("Failed to fetch Cognito user {}", email, e);
            throw new AppException(UserErrorCode.COGNITO_OPERATION_FAILED);
        }
    }
}
