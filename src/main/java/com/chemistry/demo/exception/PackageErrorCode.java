package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PackageErrorCode implements IErrorCode {
    PACKAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Package not found");

    private final HttpStatus httpStatus;
    private final String message;

    PackageErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
