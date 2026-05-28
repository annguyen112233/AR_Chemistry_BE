package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements IErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role not found"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    COGNITO_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Cognito user not found"),
    COGNITO_OPERATION_FAILED(HttpStatus.BAD_GATEWAY, "Cognito operation failed"),
    CANNOT_DELETE_LAST_ADMIN(HttpStatus.BAD_REQUEST, "Cannot delete or remove the last active admin"),
    ROLE_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST, "Role already assigned"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "Invalid role selection"),
    TEACHER_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "Teacher already approved");

    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
