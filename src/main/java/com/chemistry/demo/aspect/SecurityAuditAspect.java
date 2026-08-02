package com.chemistry.demo.aspect;

import com.chemistry.demo.logging.PerformanceLog;
import com.chemistry.demo.logging.service.ServiceExecutionContext;
import com.chemistry.demo.logging.service.ServiceExecutionContextResolver;
import com.chemistry.demo.logging.service.ServiceLogEmitter;
import com.chemistry.demo.logging.service.ServiceLogEventFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

/**
 * Ghi audit truy cập cho mọi method được bảo vệ bằng {@code @PreAuthorize}.
 * <p>Đặt ở HIGHEST_PRECEDENCE để bọc NGOÀI interceptor kiểm quyền của Spring
 * Security, nhờ đó bắt được {@link AccessDeniedException} khi bị từ chối và ghi
 * eventType=ACCESS_DENIED (phục vụ cảnh báo dò quyền admin), đồng thời ghi
 * ACCESS_GRANTED khi cho phép.
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class SecurityAuditAspect {

    private final ServiceExecutionContextResolver contextResolver;
    private final ServiceLogEventFactory eventFactory;
    private final ServiceLogEmitter logEmitter;

    @Around("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public Object auditAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        ServiceExecutionContext context = contextResolver.resolve(joinPoint);
        String actor = AuditActor.current();
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            PerformanceLog granted = eventFactory.security(context, actor, true, null, startTime);
            logEmitter.emit(granted, null);
            return result;
        } catch (AccessDeniedException ex) {
            PerformanceLog denied = eventFactory.security(context, actor, false, ex.getMessage(), startTime);
            logEmitter.emit(denied, null);
            throw ex;
        }
    }
}
