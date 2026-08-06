package com.chemistry.demo.services.aws;

import java.util.Optional;

public interface CognitoService {
    void addUserToGroup(String email, String groupName);
    void addUserToGroupIfNeeded(String email, String groupName);
    void removeUserFromGroup(String email, String groupName);
    void enableUser(String email);
    void disableUser(String email);
    void globalSignOut(String email);
    void resetPassword(String email);
    String createUser(String email, String password);
    Optional<String> getUserSubByEmail(String email);
}
