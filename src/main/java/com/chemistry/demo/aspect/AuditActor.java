package com.chemistry.demo.aspect;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tiện ích lấy "ai đang thực hiện hành động" (Cognito sub) từ SecurityContext
 * mà không cần truy vấn DB, dùng chung cho các aspect audit/security.
 */
final class AuditActor {

    private AuditActor() {
    }

    static String current() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        String name = authentication.getName();
        return (name == null || name.isBlank()) ? "anonymous" : name;
    }
}
