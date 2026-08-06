package com.chemistry.demo.services.admin;

import com.chemistry.demo.dto.response.admin.LogEntryResponse;
import com.chemistry.demo.entity.SystemLog;
import com.chemistry.demo.repository.SystemLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Đọc log hệ thống/audit từ bảng {@code system_logs} trong Postgres cho Admin
 * Portal — thay cho ElasticsearchLogService của bộ ELK cũ. Giữ nguyên hợp đồng
 * trả về {@code {items, total}} nên FE không phải đổi gì.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseLogService {

    private static final int MAX_SIZE = 200;

    private final SystemLogRepository repository;

    /**
     * @param level   ALL | INFO | WARN | ERROR | DEBUG
     * @param query   tìm chữ tự do trong message/className/methodName
     * @param minutes cửa sổ thời gian tính bằng phút
     */
    @Transactional(readOnly = true)
    public Map<String, Object> searchLogs(
            String level,
            String query,
            int minutes,
            int page,
            int size) {

        int safeSize = Math.min(Math.max(size, 1), MAX_SIZE);
        Instant since = Instant.now()
                .minus(Duration.ofMinutes(Math.max(minutes, 1)));

        Specification<SystemLog> spec = (root, q, cb) -> {
            List<Predicate> must = new ArrayList<>();
            must.add(cb.greaterThanOrEqualTo(root.get("loggedAt"), since));

            if (level != null && !level.isBlank()
                    && !"ALL".equalsIgnoreCase(level)) {
                must.add(cb.equal(root.get("level"), level.toUpperCase()));
            }

            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase() + "%";
                must.add(cb.or(
                        cb.like(cb.lower(cb.coalesce(root.get("message"), "")), like),
                        cb.like(cb.lower(cb.coalesce(root.get("className"), "")), like),
                        cb.like(cb.lower(cb.coalesce(root.get("methodName"), "")), like)));
            }

            return cb.and(must.toArray(new Predicate[0]));
        };

        Page<SystemLog> result = repository.findAll(
                spec,
                PageRequest.of(
                        Math.max(page, 0),
                        safeSize,
                        Sort.by(Sort.Direction.DESC, "loggedAt")));

        List<LogEntryResponse> items = result.getContent().stream()
                .map(this::toResponse)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", result.getTotalElements());
        return response;
    }

    private LogEntryResponse toResponse(SystemLog entry) {
        return LogEntryResponse.builder()
                .timestamp(entry.getLoggedAt() != null
                        ? entry.getLoggedAt().toString()
                        : null)
                .level(entry.getLevel() != null ? entry.getLevel() : "INFO")
                .message(entry.getMessage())
                .className(entry.getClassName())
                .methodName(entry.getMethodName())
                .durationMs(entry.getDurationMs())
                .correlationId(entry.getCorrelationId())
                .build();
    }
}
