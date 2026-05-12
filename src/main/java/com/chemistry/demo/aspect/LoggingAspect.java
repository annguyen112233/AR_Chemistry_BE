package com.chemistry.demo.aspect;

import com.chemistry.demo.untils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * SOLID: Responsibility - Tracing data flow (Input Parameters and Return
 * Values).
 * This aspect provides visibility into what data is moving through the service
 * layer.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.chemistry.demo.services..*) && !@annotation(com.chemistry.demo.aspect.NoLogging)")
    public void serviceLayerPointcut() {
    }

    @Before("serviceLayerPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        Map<String, Object> params = getParameters(joinPoint);
        log.info("SERVICE-IN  | Method: {} | Params: {}", methodName, LogUtil.toJson(params));
    }

    @AfterReturning(pointcut = "serviceLayerPointcut()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        log.info("SERVICE-OUT | Method: {} | Result: {}", methodName, LogUtil.toJson(result));
    }

    @AfterThrowing(pointcut = "serviceLayerPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        log.error("SERVICE-FAIL | Method: {} | Reason: {}", methodName, exception.getMessage());
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
