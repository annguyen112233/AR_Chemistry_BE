package com.chemistry.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Creates a request-scoped traceId and stores it in MDC.
 * The same ID is returned to the caller through X-Correlation-Id.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String REQUEST_ID_HEADER = "X-Request-Id";
    public static final String TRACE_ID_KEY = "traceId";
    public static final String REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String traceId = request.getHeader(CORRELATION_ID_HEADER);
        if (!StringUtils.hasText(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        String requestId = request.getHeader(REQUEST_ID_HEADER);

        MDC.put(TRACE_ID_KEY, traceId);
        if (StringUtils.hasText(requestId)) {
            MDC.put(REQUEST_ID_KEY, requestId);
        }
        response.setHeader(CORRELATION_ID_HEADER, traceId);
        if (StringUtils.hasText(requestId)) {
            response.setHeader(REQUEST_ID_HEADER, requestId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
            MDC.remove(REQUEST_ID_KEY);
        }
    }
}
