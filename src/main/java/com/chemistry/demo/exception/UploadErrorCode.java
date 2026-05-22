package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UploadErrorCode implements IErrorCode {
    UPLOAD_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "Upload type is required"),
    FILE_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "File name is required"),
    DANGEROUS_FILE_TYPE(HttpStatus.BAD_REQUEST, "Upload of dangerous file types is not allowed"),
    CONTENT_TYPE_REQUIRED(HttpStatus.BAD_REQUEST, "Content type is required"),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "Content type is not allowed for this upload type");

    private final HttpStatus httpStatus;
    private final String message;

    UploadErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
