package com.chemistry.demo.enums;

public enum QuizAttemptStatus {

    /**
     * User đã tạo phiên nhưng chưa hoàn thành phản ứng AR.
     * Timer chưa chạy.
     */
    WAITING_AR,

    /**
     * AR thành công.
     * Timer 7 phút đang chạy.
     */
    RUNNING,

    /**
     * User chủ động bấm nộp bài.
     */
    SUBMITTED,

    /**
     * Hết 7 phút và hệ thống tự động nộp.
     */
    TIMEOUT,

    /**
     * User thoát phiên giữa chừng trước khi nộp.
     * Không hiển thị trong lịch sử kết quả.
     */
    ABANDONED
}