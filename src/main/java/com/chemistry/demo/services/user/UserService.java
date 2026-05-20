package com.chemistry.demo.services.user;

import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;

import java.util.List;

public interface UserService {
    UserResponse syncUser(String email,String cognitoUsername,  String cognitoSub);

    UpdateProfileResponse updateProfile(UpdateProfileRequest request);

    boolean existsByEmail(String email);
    void save(User user);


}
