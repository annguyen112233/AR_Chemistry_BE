package com.chemistry.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * SOLID: Responsibility - Monitoring execution time and enforcing Performance SLAs.
 */
@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    private static final long SLOW_THRESHOLD_MS = 1000;

    @Pointcut("within(com.chemistry.demo.services..*) && !@annotation(com.chemistry.demo.aspect.NoLogging)")
    public void serviceLayerPointcut() {}

    @Around("serviceLayerPointcut()")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String requestContext = getRequestUri();
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            return joinPoint.proceed();
        } finally {
            if (stopWatch.isRunning()) stopWatch.stop();
            long duration = stopWatch.getTotalTimeMillis();
            
            if (duration > SLOW_THRESHOLD_MS) {
                log.warn("PERF-SLA-VIOLATION | {} | Method: {} | Duration: {}ms", 
                        requestContext, methodName, duration);
            } else {
                log.debug("PERF-METRIC | {} | Method: {} | Duration: {}ms", 
                        requestContext, methodName, duration);
            }
        }
    }

    private String getRequestUri() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return String.format("[%s %s]", request.getMethod(), request.getRequestURI());
        }
        return "[INTERNAL]";
    }
}
