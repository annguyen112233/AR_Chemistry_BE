package com.chemistry.demo.controller;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.dto.ArAssetResponse;
import com.chemistry.demo.services.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ar-assets")
@RequiredArgsConstructor
public class ArAssetController {

    private final S3Service s3Service;

    @GetMapping("/latest")
    public ApiResponse<ArAssetResponse> getLatestArAssets() {
        String markerKey = "ar-assets/markers/marker_bundle_v1.zip";
        String reactionKey = "ar-assets/reactions/reaction_bundle_v2.zip";

        ArAssetResponse response = ArAssetResponse.builder()
                .markerVersion(1)
                .reactionVersion(2)
                .markerUrl(s3Service.generatePresignedGetUrl(markerKey))
                .reactionUrl(s3Service.generatePresignedGetUrl(reactionKey))
                .markerSizeBytes(s3Service.getObjectSize(markerKey))
                .reactionSizeBytes(s3Service.getObjectSize(reactionKey))
                .build();

        return ApiResponse.<ArAssetResponse>ok()
                .data(response)
                .build();
    }
}