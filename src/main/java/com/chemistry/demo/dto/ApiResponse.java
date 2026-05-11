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

        /**
         * Điểm bắt đầu cho phản hồi thành công (200 OK)
         */
        public static <T> ApiResponseBuilder<T> ok() {
                return ApiResponse.<T>builder()
                                .success(true)
                                .code(HttpStatus.OK.value())
                                .message("Success");
        }

        /**
         * Điểm bắt đầu cho phản hồi tạo mới thành công (201 Created)
         */
        public static <T> ApiResponseBuilder<T> created() {
                return ApiResponse.<T>builder()
                                .success(true)
                                .code(HttpStatus.CREATED.value())
                                .message("Created Successfully");
        }

        /**
         * Điểm bắt đầu cho phản hồi lỗi
         */
        public static <T> ApiResponseBuilder<T> error() {
                return ApiResponse.<T>builder()
                                .success(false);
        }
}