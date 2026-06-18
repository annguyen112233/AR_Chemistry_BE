package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.request.upload.UploadPurposeRequest;
import com.chemistry.demo.dto.response.upload.UploadPurposeResponse;
import com.chemistry.demo.entity.UploadPurpose;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UploadPurposeMapper {
    @Mapping(target = "id", ignore = true)
    UploadPurpose toEntity(UploadPurposeRequest request);

    UploadPurposeResponse toResponse(UploadPurpose entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(UploadPurposeRequest request, @MappingTarget UploadPurpose entity);
}
