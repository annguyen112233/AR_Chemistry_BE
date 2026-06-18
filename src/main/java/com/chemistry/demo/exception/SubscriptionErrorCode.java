package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum SubscriptionErrorCode implements IErrorCode {
    FEATURE_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "The requested feature is not available in the current subscription plan. " +
            "Please upgrade your subscription to access this feature.");

    private final HttpStatus httpStatus;
    private final String message;

    SubscriptionErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
