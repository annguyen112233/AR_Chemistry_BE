package com.chemistry.demo.dto.response.chemical;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CardImageUploadUrlResponse {
    private String frontUploadUrl;
    private String backUploadUrl;

    private String frontImageKey;
    private String backImageKey;
}
