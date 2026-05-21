package com.chemistry.demo.mapper;

import com.chemistry.demo.dto.request.upload.UploadPurposeRequest;
import com.chemistry.demo.dto.response.upload.UploadPurposeResponse;
import com.chemistry.demo.entity.UploadPurpose;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UploadPurposeMapper {
    UploadPurpose toEntity(UploadPurposeRequest request);

    UploadPurposeResponse toResponse(UploadPurpose entity);

    void updateEntityFromRequest(UploadPurposeRequest request, @MappingTarget UploadPurpose entity);
}
