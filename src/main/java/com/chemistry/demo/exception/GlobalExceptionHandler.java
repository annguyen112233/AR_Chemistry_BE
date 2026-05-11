package com.chemistry.demo.exception;

import com.chemistry.demo.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(AppException.class)
        public ResponseEntity<ApiResponse<Object>> handleAppException(
                        AppException exception,
                        HttpServletRequest request) {
                ErrorCode errorCode = exception.getErrorCode();

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(ApiResponse.error(
                                                errorCode.getCode(),
                                                errorCode.getMessage(),
                                                request.getRequestURI()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleException(
                        Exception ex,
                        HttpServletRequest request) {
                ex.printStackTrace();

                ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;

                return ResponseEntity.status(errorCode.getHttpStatus())
                                .body(ApiResponse.error(
                                                errorCode.getCode(),
                                                ex.getMessage(),
                                                request.getRequestURI()));
        }
}