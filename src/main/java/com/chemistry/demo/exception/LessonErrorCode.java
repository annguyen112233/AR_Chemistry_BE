package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LessonErrorCode implements IErrorCode {
    LESSON_NOT_FOUND(HttpStatus.NOT_FOUND, "Lesson not found");

    private final HttpStatus httpStatus;
    private final String message;

    LessonErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
