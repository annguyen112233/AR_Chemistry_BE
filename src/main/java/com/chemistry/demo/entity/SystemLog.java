package com.chemistry.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Log hệ thống + audit lưu thẳng trong Postgres — thay cho bộ ELK cũ
 * (Elasticsearch/Logstash tốn RAM và chi phí vận hành, đã gỡ khỏi dự án).
 * Bảng được dọn định kỳ theo {@code SystemLogPersister#purgeOldLogs()}.
 */
@Entity
@Table(name = "system_logs", indexes = {
        @Index(name = "idx_system_logs_logged_at", columnList = "logged_at"),
        @Index(name = "idx_system_logs_level", columnList = "level"),
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    @Column(length = 10)
    private String level;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 255)
    private String className;

    @Column(length = 150)
    private String methodName;

    private Long durationMs;

    @Column(length = 100)
    private String eventType;

    /** Email/sub của người thực hiện — cột chính cho audit log. */
    @Column(length = 150)
    private String actor;

    @Column(length = 255)
    private String endpoint;

    @Column(length = 10)
    private String httpMethod;

    private Boolean success;

    @Column(length = 255)
    private String errorClass;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(length = 100)
    private String correlationId;
}
