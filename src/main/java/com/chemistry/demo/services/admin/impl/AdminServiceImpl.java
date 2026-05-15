package com.chemistry.demo.services.admin.impl;

import com.chemistry.demo.services.admin.AdminDashboardService;
import com.chemistry.demo.services.admin.AdminService;
import com.chemistry.demo.services.admin.AdminUserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminUserManagementService userManagementService;
    private final AdminDashboardService dashboardService;

    @Override
    public AdminUserManagementService userManagement() {
        return userManagementService;
    }

    @Override
    public AdminDashboardService dashboard() {
        return dashboardService;
    }
}
