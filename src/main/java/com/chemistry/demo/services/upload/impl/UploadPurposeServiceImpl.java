package com.chemistry.demo.services.upload.impl;

import com.chemistry.demo.dto.request.upload.UploadPurposeRequest;
import com.chemistry.demo.dto.response.upload.UploadPurposeResponse;
import com.chemistry.demo.entity.UploadPurpose;
import com.chemistry.demo.exception.AppException;
import com.chemistry.demo.exception.UploadErrorCode;
import com.chemistry.demo.mapper.UploadPurposeMapper;
import com.chemistry.demo.repository.UploadPurposeRepository;
import com.chemistry.demo.services.upload.UploadPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadPurposeServiceImpl implements UploadPurposeService {

    private final UploadPurposeRepository uploadPurposeRepository;
    private final UploadPurposeMapper uploadPurposeMapper;

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UploadPurposeResponse create(UploadPurposeRequest request) {
        if (uploadPurposeRepository.findByCodeAndActiveTrue(request.getCode()).isPresent()) {
            throw new AppException(UploadErrorCode.UPLOAD_PURPOSE_ALREADY_EXISTS);
        }

        UploadPurpose entity = uploadPurposeMapper.toEntity(request);
        if (entity.getActive() == null) {
            entity.setActive(true);
        }
        return uploadPurposeMapper.toResponse(uploadPurposeRepository.save(entity));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UploadPurposeResponse update(Long id, UploadPurposeRequest request) {
        UploadPurpose purpose = uploadPurposeRepository.findById(id)
                .orElseThrow(() -> new AppException(UploadErrorCode.UPLOAD_PURPOSE_NOT_FOUND));

        if (!purpose.getCode().equals(request.getCode()) &&
                uploadPurposeRepository.findByCodeAndActiveTrue(request.getCode()).isPresent()) {
            throw new AppException(UploadErrorCode.UPLOAD_PURPOSE_ALREADY_EXISTS);
        }

        uploadPurposeMapper.updateEntityFromRequest(request, purpose);
        return uploadPurposeMapper.toResponse(uploadPurposeRepository.save(purpose));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(Long id) {
        UploadPurpose purpose = uploadPurposeRepository.findById(id)
                .orElseThrow(() -> new AppException(UploadErrorCode.UPLOAD_PURPOSE_NOT_FOUND));
        uploadPurposeRepository.delete(purpose);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UploadPurposeResponse getById(Long id) {
        return uploadPurposeRepository.findById(id)
                .map(uploadPurposeMapper::toResponse)
                .orElseThrow(() -> new AppException(UploadErrorCode.UPLOAD_PURPOSE_NOT_FOUND));
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<UploadPurposeResponse> getAll(Pageable pageable) {
        return uploadPurposeRepository.findAll(pageable)
                .map(uploadPurposeMapper::toResponse);
    }
}
