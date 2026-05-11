package com.chemistry.demo.services.user;

import com.chemistry.demo.dto.request.UpdateProfileRequest;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;

public interface UserService {
    UserResponse syncUser(String email, String cognitoSub);

    UpdateProfileResponse updateProfile(UpdateProfileRequest request);
}
