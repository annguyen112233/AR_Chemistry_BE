package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UploadErrorCode implements IErrorCode {
    UPLOAD_PURPOSE_NOT_FOUND(HttpStatus.NOT_FOUND, "Upload purpose not found"),
    UPLOAD_PURPOSE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Upload purpose code already exists"),
    UPLOAD_PURPOSE_INACTIVE(HttpStatus.BAD_REQUEST, "Upload purpose is inactive"),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "Invalid file name"),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "Invalid content type"),
    INVALID_FILE_SIZE(HttpStatus.BAD_REQUEST, "Invalid file size"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "File size exceeded"),
    PRESIGNED_URL_GENERATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate presigned upload url");

    private final HttpStatus httpStatus;
    private final String message;

    UploadErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
