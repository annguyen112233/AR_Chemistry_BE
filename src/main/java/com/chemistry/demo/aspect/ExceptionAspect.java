package com.chemistry.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * SOLID: Responsibility - Centralized Exception Logging for the Service Layer.
 * Intercepts any throwable and logs it with structural context for ELK stacks.
 */
@Aspect
@Component
@Slf4j
public class ExceptionAspect {

    /**
     * Pointcut captures all exceptions thrown within the services package.
     */
    @AfterThrowing(pointcut = "within(com.chemistry.demo.services..*)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("SERVICE-EXCEPTION | Location: {}.{}() | Message: {} | Cause: {}",
                className,
                methodName,
                exception.getMessage(),
                exception.getCause() != null ? exception.getCause().toString() : "NULL");
    }
}
