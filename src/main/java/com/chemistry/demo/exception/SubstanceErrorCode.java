package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SubstanceErrorCode implements IErrorCode {
    SUBSTANCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Substance with the same name already exists."),
    SUBSTANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Substance not found.");

    private final HttpStatus httpStatus;
    private final String message;

    SubstanceErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
