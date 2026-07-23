package com.chemistry.demo.dto.request.quiz.student;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompleteArRequest {

    /**
     * Danh sách mã flash card user đã quét.
     *
     * Phản ứng nhiệt phân: 1 card.
     * Phản ứng thông thường: 2 card.
     */
    @NotEmpty(message = "Scanned card codes must not be empty")
    private List<String> scannedCardCodes;

    /**
     * Unity xác nhận phản ứng đã thực hiện thành công.
     */
    @NotNull(message = "Reaction successful status is required")
    private Boolean reactionSuccessful;

    /**
     * Mã phiên AR do frontend hoặc Unity tạo.
     */
    private String arSessionCode;
}