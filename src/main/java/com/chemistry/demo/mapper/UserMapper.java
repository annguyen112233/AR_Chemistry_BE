package com.chemistry.demo.mapper;
import com.chemistry.demo.dto.response.user.AdminUsersResponse;
import com.chemistry.demo.dto.response.user.AdminUserDetailResponse;
import com.chemistry.demo.dto.response.profile.UpdateProfileResponse;
import com.chemistry.demo.dto.response.user.UserResponse;
import com.chemistry.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);

    UpdateProfileResponse toUpdateProfileResponse(User user);

    @Mapping(target = "roles", source = "roles")
    AdminUsersResponse toAdminUsersResponse(User users);

    @Mapping(target = "roles", source = "roles")
    AdminUserDetailResponse toAdminUserDetailResponse(User user);
}
