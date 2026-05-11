package com.chemistry.demo.mapper;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);

    UpdateProfileResponse toUpdateProfileResponse(User user);
}
