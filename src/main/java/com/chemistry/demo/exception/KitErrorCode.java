package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum KitErrorCode implements IErrorCode {

    KIT_NOT_FOUND(HttpStatus.NOT_FOUND, "Kit not found"),
    KIT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Kit already exists");

    private final HttpStatus httpStatus;
    private final String message;

    KitErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
