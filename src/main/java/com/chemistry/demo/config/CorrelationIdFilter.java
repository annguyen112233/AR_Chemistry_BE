package com.chemistry.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to generate and inject a Correlation ID into MDC for Kibana tracing.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_VAR = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // Lấy ID từ header (nếu client gửi lên) hoặc tự tạo mới
        String correlationId = httpServletRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Đẩy vào MDC để Logback có thể lấy ra gắn vào JSON log
        MDC.put(CORRELATION_ID_LOG_VAR, correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            // Xóa ID sau khi kết thúc request để tránh rò rỉ dữ liệu giữa các thread
            MDC.remove(CORRELATION_ID_LOG_VAR);
        }
    }
}
