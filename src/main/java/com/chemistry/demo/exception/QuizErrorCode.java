package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum QuizErrorCode implements IErrorCode {
    AR_REACTION_NOT_SUCCESSFUL(HttpStatus.BAD_REQUEST, "AR reaction was not successful"),
    INVALID_REACTION_CONFIGURATION(HttpStatus.BAD_REQUEST, "Invalid reaction configuration"),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Question not found"),
    INVALID_GRADE(HttpStatus.BAD_REQUEST, "Invalid grade"),
    AR_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "AR session is not completed"),
    CANNOT_ABANDON_COMPLETED_ATTEMPT(HttpStatus.BAD_REQUEST, "Cannot abandon a completed quiz attempt"),
    QUIZ_RESULT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Quiz result is not available"),
    INVALID_QUESTION_OPTION(HttpStatus.BAD_REQUEST, "Invalid question option"),
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "Quiz not found"),
    QUIZ_HAS_NO_QUESTIONS(HttpStatus.BAD_REQUEST, "Quiz has no questions"),
    QUIZ_CONTENT_LOCKED(HttpStatus.BAD_REQUEST, "Quiz content is locked"),
    QUIZ_ATTEMPT_NOT_FOUND(HttpStatus.NOT_FOUND,
            "Quiz attempt not found"),
    INVALID_QUIZ_CONFIGURATION(HttpStatus.BAD_REQUEST, "Invalid quiz configuration"),

    QUIZ_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "Quiz time has expired"),
    INVALID_AR_FLASH_CARDS(HttpStatus.BAD_REQUEST, "Invalid AR flash cards"),
    DUPLICATE_FLASH_CARD(HttpStatus.BAD_REQUEST, "Duplicate flash card"),
    INVALID_QUIZ_ATTEMPT_STATUS(HttpStatus.BAD_REQUEST,
            "Quiz attempt status is invalid"),
    QUIZ_ALREADY_PUBLISHED(HttpStatus.BAD_REQUEST, "Quiz is already published");

    private final HttpStatus httpStatus;
    private final String message;

    QuizErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
