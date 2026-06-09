package com.chemistry.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum PaymentErrorCode implements IErrorCode {

    INVALID_PROOF_IMAGE(HttpStatus.BAD_REQUEST, "Invalid proof image. Please upload a valid image file."),
    PAYMENT_ALREADY_PENDING(HttpStatus.BAD_REQUEST, "A payment is already pending for this package. " +
            "Please wait for the current payment to be processed before making another payment."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found. Please check the payment ID and try again."),
    INVALID_PAYMENT_ITEM_TYPE(HttpStatus.BAD_REQUEST, "Invalid payment item type. Allowed types are 'PACKAGE' and 'CHEMICAL_CARD'.");
    private final HttpStatus httpStatus;
    private final String message;

    PaymentErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
