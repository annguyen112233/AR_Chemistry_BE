package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum QuizErrorCode implements IErrorCode {
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Quiz not found"),
    QUIZ_HAS_NO_QUESTIONS(HttpStatus.BAD_REQUEST, "Quiz has no questions"),
    QUIZ_ALREADY_PUBLISHED(HttpStatus.BAD_REQUEST, "Quiz is already published");

    private final HttpStatus httpStatus;
    private final String message;

    QuizErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
