package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CardBundleErrorCode implements IErrorCode {
    CARD_BUNDLE_NOT_PURCHASABLE(HttpStatus.BAD_REQUEST, "Card bundle is not purchasable"),
    CARD_BUNDLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Card bundle not found");

    private final HttpStatus httpStatus;
    private final String message;

    CardBundleErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
