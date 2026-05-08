package com.chemistry.demo.mapper;
import com.chemistry.demo.dto.response.UpdateProfileResponse;
import com.chemistry.demo.dto.response.UserResponse;
import com.chemistry.demo.entity.User;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

    UpdateProfileResponse toUpdateProfileResponse(User user);
}
