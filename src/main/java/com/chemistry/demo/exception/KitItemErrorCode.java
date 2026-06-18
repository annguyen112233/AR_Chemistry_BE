package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum KitItemErrorCode implements IErrorCode {

    KIT_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Kit item not found"),
    KIT_ITEM_ALREADY_EXISTS(HttpStatus.CONFLICT, "Kit item already exists"),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "Invalid quantity");

    private final HttpStatus httpStatus;
    private final String message;

    KitItemErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
