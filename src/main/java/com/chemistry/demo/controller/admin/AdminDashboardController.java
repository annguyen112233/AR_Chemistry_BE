package com.chemistry.demo.controller.admin;

import com.chemistry.demo.dto.ApiResponse;
import com.chemistry.demo.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminService adminService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.<Map<String, Object>>ok()
                .data(adminService.dashboard().getDashboardStats())
                .build();
    }
}
