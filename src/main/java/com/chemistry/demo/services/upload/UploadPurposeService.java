package com.chemistry.demo.services.upload;

import com.chemistry.demo.dto.request.upload.UploadPurposeRequest;
import com.chemistry.demo.dto.response.upload.UploadPurposeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UploadPurposeService {
    UploadPurposeResponse create(UploadPurposeRequest request);

    UploadPurposeResponse update(Long id, UploadPurposeRequest request);

    void delete(Long id);

    UploadPurposeResponse getById(Long id);

    Page<UploadPurposeResponse> getAll(Pageable pageable);
}
