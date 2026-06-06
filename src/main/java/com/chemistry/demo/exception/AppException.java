package com.chemistry.demo.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final IErrorCode errorCode;

    public AppException(IErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.errorCode = errorCode;
    }

    public AppException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}