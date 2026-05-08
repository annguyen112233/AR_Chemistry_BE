package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND( "User not found", HttpStatus.NOT_FOUND),

    UNAUTHENTICATED( "Unauthenticated", HttpStatus.UNAUTHORIZED),

    FORBIDDEN("Forbidden", HttpStatus.FORBIDDEN);

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(
            String message,
            HttpStatus httpStatus
    ) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}