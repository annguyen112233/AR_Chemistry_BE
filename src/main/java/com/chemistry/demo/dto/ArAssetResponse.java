package com.chemistry.demo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArAssetResponse {
    private int markerVersion;
    private int reactionVersion;
    private String markerUrl;
    private String reactionUrl;
    private Long markerSizeBytes;
    private Long reactionSizeBytes;
}