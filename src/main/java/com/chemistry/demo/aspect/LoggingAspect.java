package com.chemistry.demo.aspect;

import com.chemistry.demo.logging.PerformanceLog;
import com.chemistry.demo.logging.service.ServiceExecutionContext;
import com.chemistry.demo.logging.service.ServiceExecutionContextResolver;
import com.chemistry.demo.logging.service.ServiceLogEventFactory;
import com.chemistry.demo.logging.service.ServiceLogEmitter;
import com.chemistry.demo.utils.SeederExecutionContext;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class LoggingAspect {

    private final ServiceExecutionContextResolver contextResolver;
    private final ServiceLogEventFactory eventFactory;
    private final ServiceLogEmitter logEmitter;

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

        ServiceExecutionContext context = contextResolver.resolve(joinPoint);
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            PerformanceLog event = eventFactory.success(context, result, startTime);
            logEmitter.emit(event, null);
            return result;
        } catch (Throwable ex) {
            PerformanceLog event = eventFactory.failure(context, ex, startTime);
            logEmitter.emit(event, ex);
            throw ex;
        }
    }
}
