package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AiErrorCode implements IErrorCode {
    AI_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI service is currently unavailable"),
    CONVERSATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Conversation not found");

    private final HttpStatus httpStatus;
    private final String message;

    AiErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
