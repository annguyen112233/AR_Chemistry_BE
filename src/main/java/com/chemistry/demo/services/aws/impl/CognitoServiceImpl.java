package com.chemistry.demo.services.aws.impl;

import com.chemistry.demo.services.aws.CognitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;

@Service
@RequiredArgsConstructor
public class CognitoServiceImpl implements CognitoService {

    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${aws.user_pool-id}")
    private String userPoolId;

    @Override
    public void addUserToGroup(String email, String groupName) {
        AdminAddUserToGroupRequest groupRequest = AdminAddUserToGroupRequest.builder()
                .groupName(groupName)
                .userPoolId(userPoolId)
                .username(email)
                .build();

        cognitoClient.adminAddUserToGroup(groupRequest);
    }
}
