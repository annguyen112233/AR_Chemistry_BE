package com.chemistry.demo.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Một dòng log đọc từ Elasticsearch (index ar-chemistry-be-logs-*).
 * Tên field khớp với output của logstash.conf sau bước filter/rename.
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
