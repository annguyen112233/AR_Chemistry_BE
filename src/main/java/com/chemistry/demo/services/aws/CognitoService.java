package com.chemistry.demo.services.aws;

public interface CognitoService {
    void addUserToGroup(String email, String groupName);
}
