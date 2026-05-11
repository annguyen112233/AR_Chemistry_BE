package com.chemistry.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

        @Builder.Default
        private boolean success = true;

        private int code;

        private String message;

        private T data;

        @Builder.Default
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime timestamp = LocalDateTime.now();

        private String path;

        // --- Static Factory Methods ---

        public static <T> ApiResponse<T> ok(T data) {
                return buildResponse(HttpStatus.OK, "Success", data);
        }

        public static <T> ApiResponse<T> ok(String message, T data) {
                return buildResponse(HttpStatus.OK, message, data);
        }

        public static <T> ApiResponse<T> ok(String message) {
                return buildResponse(HttpStatus.OK, message, null);
        }

        public static <T> ApiResponse<T> created(String message, T data) {
                return buildResponse(HttpStatus.CREATED, message, data);
        }

        public static <T> ApiResponse<T> error(int code, String message, String path) {
                return ApiResponse.<T>builder()
                                .success(false)
                                .code(code)
                                .message(message)
                                .path(path)
                                .build();
        }

        // Helper method to reduce boilerplate
        private static <T> ApiResponse<T> buildResponse(HttpStatus status, String message, T data) {
                return ApiResponse.<T>builder()
                                .code(status.value())
                                .message(message)
                                .data(data)
                                .build();
        }
}