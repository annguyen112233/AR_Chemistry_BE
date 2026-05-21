package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FeedbackErrorCode implements IErrorCode {
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "Feedback not found");

    private final HttpStatus httpStatus;
    private final String message;

    FeedbackErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
