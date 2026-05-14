package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.role.RoleResponse;
import com.chemistry.demo.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "roleName", source = "roleName")
    RoleResponse toResponse(Role role);

}
