package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ReactionErrorCode implements IErrorCode {

    REACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Reaction not found"),
    REACTION_ALREADY_EXISTS(HttpStatus.CONFLICT, "Reaction already exists");

    private final HttpStatus httpStatus;
    private final String message;

    ReactionErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
