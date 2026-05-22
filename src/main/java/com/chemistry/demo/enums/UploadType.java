package com.chemistry.demo.enums;

import lombok.Getter;

@Getter
public enum UploadType {
    FEEDBACK("feedback/"),
    AVATAR("users/avatars/"),
    RESEARCH_PAPER("papers/"),
    EVIDENCE("integrity/evidence/"),
    REPORT("reports/"),
    DATASET("datasets/"),
    THUMBNAIL("thumbnails/"),
    PUBLICATION_FILE("publications/files/");

    private final String folderPath;

    UploadType(String folderPath) {
        this.folderPath = folderPath;
    }
}
