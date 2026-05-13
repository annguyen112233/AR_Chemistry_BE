package com.chemistry.demo.logging.service;

import com.chemistry.demo.utils.LogUtil;
import com.chemistry.demo.utils.SanitizerUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ServiceExecutionContextResolver {

    public ServiceExecutionContext resolve(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = resolveTargetClass(joinPoint, signature);
        String serviceName = LogUtil.simpleClassName(targetClass);
        String module = LogUtil.moduleFromClassPackage(targetClass);
        String methodName = signature.getName();
        String traceId = LogUtil.currentTraceId();
        String requestId = LogUtil.currentRequestId();
        String httpMethod = LogUtil.currentHttpMethod();
        String endpoint = LogUtil.currentEndpoint();

        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        Map<String, Object> safeParams = SanitizerUtil.safeParamMap(parameterNames, args);
        List<String> paramTypes = Arrays.stream(signature.getParameterTypes())
                .map(Class::getSimpleName)
                .toList();

        return new ServiceExecutionContext(
                targetClass,
                serviceName,
                module,
                methodName,
                traceId,
                requestId,
                httpMethod,
                endpoint,
                args != null ? args.length : 0,
                paramTypes,
                safeParams
        );
    }

    private Class<?> resolveTargetClass(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        if (joinPoint.getTarget() != null) {
            return AopUtils.getTargetClass(joinPoint.getTarget());
        }
        return signature.getDeclaringType();
    }
}
