package com.chemistry.demo.dto.request.chemical;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardImageUploadUrlRequest {
    private String frontContentType;
    private Long frontFileSize;

    private String backContentType;
    private Long backFileSize;
}
