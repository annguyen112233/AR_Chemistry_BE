package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

        UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Uncategorized error"),
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
        ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found"),
        UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Unauthenticated"),
        FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
        ROLE_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "Role already assigned"),
        INVALID_ROLE(HttpStatus.BAD_REQUEST, "Invalid role selection"),
        TEACHER_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "Teacher already approved");

        private final String message;
        private final HttpStatus httpStatus;

        ErrorCode(HttpStatus status, String message) {
                this.message = message;
                this.httpStatus = status;
        }
}