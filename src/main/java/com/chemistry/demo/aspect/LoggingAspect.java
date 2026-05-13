package com.chemistry.demo.aspect;

import com.chemistry.demo.logging.PerformanceLog;
import com.chemistry.demo.utils.LogUtil;
import com.chemistry.demo.utils.SeederExecutionContext;
import com.chemistry.demo.utils.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Aspect
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class LoggingAspect {

    private static final long SLOW_THRESHOLD_MS = 500L;
    private static final String APPLICATION = "AR_Labs";
    private static final String LAYER = "SERVICE";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String EVENT_SERVICE_EXECUTION = "SERVICE_EXECUTION";
    private static final String EVENT_SERVICE_EXCEPTION = "SERVICE_EXCEPTION";
    private static final String EVENT_SLOW_SERVICE = "SLOW_SERVICE";

    private final Environment environment;

    @Pointcut("execution(* com.chemistry.demo.services..*(..))" +
            " && !@within(com.chemistry.demo.aspect.NoLogging)" +
            " && !@annotation(com.chemistry.demo.aspect.NoLogging)")
    public void serviceLayerPointcut() {
    }

    @Around("serviceLayerPointcut()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        if (SeederExecutionContext.isActive()) {
            return joinPoint.proceed();
        }

        long startTime = System.nanoTime();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = resolveTargetClass(joinPoint, signature);
        String className = LogUtil.simpleClassName(targetClass);
        String module = LogUtil.moduleFromClassPackage(targetClass);
        String methodName = signature.getName();
        String traceId = LogUtil.currentTraceId();
        String requestId = LogUtil.currentRequestId();
        String httpMethod = LogUtil.currentHttpMethod();
        String endpoint = LogUtil.currentEndpoint();

        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> safeParams = SanitizerUtil.safeParamMap(parameterNames, args);
        List<String> paramTypes = List.of(signature.getParameterTypes())
                .stream()
                .map(type -> type != null ? type.getSimpleName() : null)
                .filter(type -> type != null && !type.isBlank())
                .toList();

        try {
            Object result = joinPoint.proceed();
            long durationMs = LogUtil.durationMs(startTime);
            PerformanceLog event = buildBaseEvent(className, module, methodName, durationMs, traceId, requestId, httpMethod, endpoint)
                    .toBuilder()
                    .status(STATUS_SUCCESS)
                    .eventType(durationMs > SLOW_THRESHOLD_MS ? EVENT_SLOW_SERVICE : EVENT_SERVICE_EXECUTION)
                    .level(durationMs > SLOW_THRESHOLD_MS ? "WARN" : "INFO")
                    .message(durationMs > SLOW_THRESHOLD_MS ? "Slow service detected" : "Service executed")
                    .success(true)
                    .paramCount(args != null ? args.length : 0)
                    .paramTypes(paramTypes)
                    .safeParams(safeParams.isEmpty() ? null : safeParams)
                    .resultType(SanitizerUtil.extractResultType(result))
                    .resultSize(SanitizerUtil.extractResultSize(result))
                    .resultId(SanitizerUtil.extractResultId(result))
                    .build();

            logEvent(event, null);
            return result;
        } catch (Throwable ex) {
            long durationMs = LogUtil.durationMs(startTime);
            PerformanceLog event = buildBaseEvent(className, module, methodName, durationMs, traceId, requestId, httpMethod, endpoint)
                    .toBuilder()
                    .status(STATUS_FAILED)
                    .eventType(EVENT_SERVICE_EXCEPTION)
                    .level("ERROR")
                    .message("Service failed")
                    .success(false)
                    .errorClass(ex.getClass().getSimpleName())
                    .errorMessage(SanitizerUtil.sanitizeErrorMessage(ex.getMessage()))
                    .rootCause(SanitizerUtil.sanitizeRootCause(ex))
                    .paramCount(args != null ? args.length : 0)
                    .paramTypes(paramTypes)
                    .safeParams(safeParams.isEmpty() ? null : safeParams)
                    .build();

            logEvent(event, ex);
            throw ex;
        }
    }

    private PerformanceLog buildBaseEvent(String className,
                                          String module,
                                          String methodName,
                                          long durationMs,
                                          String traceId,
                                          String requestId,
                                          String httpMethod,
                                          String endpoint) {
        return PerformanceLog.builder()
                .application(APPLICATION)
                .environment(LogUtil.resolveEnvironment(environment))
                .timestamp(Instant.now())
                .traceId(traceId)
                .requestId(requestId)
                .serviceName(className)
                .module(module)
                .layer(LAYER)
                .className(className)
                .methodName(methodName)
                .durationMs(durationMs)
                .httpMethod(httpMethod)
                .endpoint(endpoint)
                .build();
    }

    private void logEvent(PerformanceLog event, Throwable throwable) {
        var marker = Markers.appendEntries(event.toMap());
        if (throwable != null) {
            log.error(marker, event.getMessage(), throwable);
            return;
        }

        if ("WARN".equals(event.getLevel())) {
            log.warn(marker, event.getMessage());
        } else {
            log.info(marker, event.getMessage());
        }
    }

    private Class<?> resolveTargetClass(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        if (joinPoint.getTarget() != null) {
            return AopUtils.getTargetClass(joinPoint.getTarget());
        }
        return signature.getDeclaringType();
    }
}
