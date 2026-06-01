package com.chemistry.demo.controller;

import com.chemistry.demo.dto.request.upload.UploadPurposeRequest;
import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.response.upload.UploadPurposeResponse;
import com.chemistry.demo.services.upload.UploadPurposeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/upload-purposes")
@RequiredArgsConstructor
public class UploadPurposeController {

    private final UploadPurposeService uploadPurposeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UploadPurposeResponse> create(@RequestBody @Valid UploadPurposeRequest request) {
        return ApiResponse.<UploadPurposeResponse>builder()
                .data(uploadPurposeService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UploadPurposeResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UploadPurposeRequest request) {
        return ApiResponse.<UploadPurposeResponse>builder()
                .data(uploadPurposeService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        uploadPurposeService.delete(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<UploadPurposeResponse> getById(@PathVariable Long id) {
        return ApiResponse.<UploadPurposeResponse>builder()
                .data(uploadPurposeService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<UploadPurposeResponse>> getAll(Pageable pageable) {
        return ApiResponse.<Page<UploadPurposeResponse>>builder()
                .data(uploadPurposeService.getAll(pageable))
                .build();
    }
}
