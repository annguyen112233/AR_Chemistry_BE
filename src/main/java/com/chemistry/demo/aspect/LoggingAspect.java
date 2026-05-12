package com.chemistry.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.chemistry.demo.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * SOLID: Responsibility - Tracing data flow (Input Parameters and Return
 * Values).
 * 
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("(within(com.chemistry.demo.services..*) || within(com.chemistry.demo.controller..*) || (within(com.chemistry.demo.config..*) && !within(com.chemistry.demo.config.seeder..*)))"
            +
            " && !@within(com.chemistry.demo.aspect.NoLogging)" +
            " && !@annotation(com.chemistry.demo.aspect.NoLogging)")
    public void applicationLayerPointcut() {
    }

    @Before("applicationLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Map<String, Object> params = getParameters(joinPoint);

        log.info("AOP-TRACE | START | Method: {}.{}() | Params: {}",
                className, methodName, LogUtil.toJson(params));
    }

    @AfterReturning(pointcut = "applicationLayerPointcut()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        log.info("AOP-TRACE | SUCCESS | Method: {}.{}() | Result: {}",
                className, methodName, LogUtil.toJson(result));
    }

    @AfterThrowing(pointcut = "applicationLayerPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        log.error("AOP-TRACE | FAILED | Method: {}.{}() | Error: {}",
                className, methodName, exception.getMessage());
    }

    private Map<String, Object> getParameters(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> params = new HashMap<>();
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                params.put(parameterNames[i], args[i]);
            }
        }
        return params;
    }
}
