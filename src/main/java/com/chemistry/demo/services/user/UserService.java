package com.chemistry.demo.services.user;

import com.chemistry.demo.dto.request.profile.UpdateProfileRequest;
import com.chemistry.demo.dto.response.profile.UpdateProfileResponse;
import com.chemistry.demo.dto.response.user.UserResponse;
import com.chemistry.demo.dto.response.profile.UserProfileResponse;
import com.chemistry.demo.entity.User;

public interface UserService {
    UserResponse syncUser(String email,String cognitoUsername,  String cognitoSub);

    UpdateProfileResponse updateProfile(UpdateProfileRequest request);

    boolean existsByEmail(String email);
    void save(User user);

    UserProfileResponse getUserProfile();

    String updateAvatar(String avatarUrl);


}
