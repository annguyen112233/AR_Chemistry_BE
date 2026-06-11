package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ActivationCodeErrorCode implements IErrorCode {
    ACTIVATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "Activation code not found"),
    ACTIVATION_CODE_INACTIVE(HttpStatus.BAD_REQUEST, "Activation code is inactive"),
    ACTIVATION_CODE_ALREADY_USED(HttpStatus.BAD_REQUEST, "Activation code has already been used"),
    ACTIVATION_CODE_IS_LOCKED(HttpStatus.BAD_REQUEST, "Activation code is locked"),
    ACTIVATION_CODE_IS_EXPIRED(HttpStatus.BAD_REQUEST, "Activation code is expired"),
    ACTIVATION_CODE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Activation code is not available"),
    CANNOT_UPDATE_USED_ACTIVATION_CODE(HttpStatus.BAD_REQUEST, "Cannot update status of an activation code that has already been used"),
    CANNOT_MARK_ACTIVATION_CODE_AS_USED_MANUALLY(HttpStatus.BAD_REQUEST, "Cannot mark an activation code as used manually");

    private final HttpStatus httpStatus;
    private final String message;

    ActivationCodeErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
