package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.profile.UserProfileResponse;
import com.chemistry.demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileResponse toUserProfileResponse(User user);
}
