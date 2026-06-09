package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ChemicalCardErrorCode implements IErrorCode {

    CHEMICAL_CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "Chemical card not found"),
    CHEMICAL_CARD_ALREADY_EXISTS(HttpStatus.CONFLICT, "Chemical card already exists"),
    CHEMICAL_CARD_NOT_PURCHASABLE(HttpStatus.BAD_REQUEST, "Chemical card is not purchasable");

    private final HttpStatus httpStatus;
    private final String message;

    ChemicalCardErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
