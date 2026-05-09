package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND(
            1001,
            "User not found",
            HttpStatus.NOT_FOUND
    ),

    ROLE_NOT_FOUND(
            1002,
            "Role not found",
            HttpStatus.NOT_FOUND
    ),

    UNAUTHENTICATED(
            1003,
            "Unauthenticated",
            HttpStatus.UNAUTHORIZED
    ),

    FORBIDDEN(
            1004,
            "Forbidden",
            HttpStatus.FORBIDDEN
    ),

    ROLE_ALREADY_ASSIGNED(
        1005,
                "Role already assigned",
        HttpStatus.BAD_REQUEST
        );

    private final int code;

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(
            int code,
            String message,
            HttpStatus httpStatus
    ) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}