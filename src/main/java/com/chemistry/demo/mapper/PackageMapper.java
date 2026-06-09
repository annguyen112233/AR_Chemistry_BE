package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.response.packages.PackageResponse;
import com.chemistry.demo.entity.Packages;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PackageMapper {
    PackageResponse toPackageResponse(Packages packages);
}
