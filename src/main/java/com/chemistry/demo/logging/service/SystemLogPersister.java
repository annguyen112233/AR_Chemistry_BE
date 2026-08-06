package com.chemistry.demo.logging.service;

import com.chemistry.demo.entity.SystemLog;
import com.chemistry.demo.repository.SystemLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

/**
 * Ghi {@link SystemLog} vào Postgres — thay cho pipeline Logstash cũ.
 *
 * - {@code @Async} + REQUIRES_NEW: ghi log không chặn request và không dính vào
 *   transaction nghiệp vụ (nghiệp vụ rollback thì log lỗi vẫn còn).
 * - Nuốt mọi exception: hệ thống log chết không được phép kéo nghiệp vụ chết theo.
 * - Dọn log cũ hơn {@link #RETENTION_DAYS} ngày lúc 3h sáng để bảng không phình.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemLogPersister {

    private static final int RETENTION_DAYS = 30;

    private final SystemLogRepository repository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persist(SystemLog entry) {
        try {
            repository.save(entry);
        } catch (Exception e) {
            log.warn("Không ghi được system log vào DB: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeOldLogs() {
        try {
            int deleted = repository.deleteOlderThan(
                    Instant.now().minus(Duration.ofDays(RETENTION_DAYS)));
            if (deleted > 0) {
                log.info("Đã dọn {} dòng system log cũ hơn {} ngày",
                        deleted, RETENTION_DAYS);
            }
        } catch (Exception e) {
            log.warn("Dọn system log thất bại: {}", e.getMessage());
        }
    }
}
