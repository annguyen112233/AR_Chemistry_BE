package com.chemistry.demo.services.aws;

import java.util.Optional;

public interface CognitoService {
    void addUserToGroup(String email, String groupName);
    void addUserToGroupIfNeeded(String email, String groupName);
    String createUser(String email, String password);
    Optional<String> getUserSubByEmail(String email);
}
