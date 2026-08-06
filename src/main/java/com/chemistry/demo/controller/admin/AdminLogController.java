package com.chemistry.demo.controller.admin;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.services.admin.DatabaseLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Xem log hệ thống/audit (bảng system_logs trong Postgres) ngay trong
 * Admin Portal. Chỉ ROLE_ADMIN gọi được.
 */
@Slf4j
@RestController
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
public class AdminLogController {

    private final DatabaseLogService logService;

    /**
     * @param level   ALL | INFO | WARN | ERROR | DEBUG
     * @param q       tìm chữ tự do trong message/className/methodName
     * @param minutes cửa sổ thời gian tính bằng phút (mặc định 24 giờ)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<Map<String, Object>> getLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1440") int minutes,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        return ApiResponse.<Map<String, Object>>ok()
                .data(logService.searchLogs(level, q, minutes, page, size))
                .build();
    }
}
