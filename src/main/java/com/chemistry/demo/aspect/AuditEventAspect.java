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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Emit sự kiện nghiệp vụ (audit) lên ELK cho các method gắn {@link AuditEvent}.
 * Tái dùng pipeline PerformanceLog/ServiceLogEmitter sẵn có; chỉ khác eventType
 * (= tên action) và có thêm actor. Ghi cả trường hợp thành công lẫn thất bại.
 */
@Aspect
@Component
@Order(4)
@RequiredArgsConstructor
public class AuditEventAspect {

    private final ServiceExecutionContextResolver contextResolver;
    private final ServiceLogEventFactory eventFactory;
    private final ServiceLogEmitter logEmitter;

    @Around("@annotation(auditEvent)")
    public Object recordAudit(ProceedingJoinPoint joinPoint, AuditEvent auditEvent) throws Throwable {
        ServiceExecutionContext context = contextResolver.resolve(joinPoint);
        String actor = AuditActor.current();
        String action = resolveAction(auditEvent, joinPoint);
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            PerformanceLog event = eventFactory.audit(context, action, actor, true, result, startTime);
            logEmitter.emit(event, null);
            return result;
        } catch (Throwable ex) {
            PerformanceLog event = eventFactory.audit(context, action, actor, false, null, startTime);
            logEmitter.emit(event, ex);
            throw ex;
        }
    }

    private String resolveAction(AuditEvent auditEvent, ProceedingJoinPoint joinPoint) {
        if (auditEvent.value() != null && !auditEvent.value().isBlank()) {
            return auditEvent.value();
        }
        return ((MethodSignature) joinPoint.getSignature()).getName();
    }
}
