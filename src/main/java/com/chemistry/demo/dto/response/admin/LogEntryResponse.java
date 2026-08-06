package com.chemistry.demo.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Một dòng log hệ thống/audit đọc từ bảng system_logs (Postgres)
 * hiển thị trong tab Logs của Admin Portal.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogEntryResponse {
    private String timestamp;
    private String level;
    private String message;
    private String className;
    private String methodName;
    private Long durationMs;
    private String correlationId;
}
