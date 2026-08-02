package com.chemistry.demo.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Đánh dấu một method để {@link AiFlowTraceAspect} trace như một bước trong
 * luồng AI/RAG. Dùng cho các method nằm ngoài package services.ai
 * (ví dụ ở controller) mà vẫn muốn xuất hiện trong log luồng.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AiFlowTrace {

    /**
     * Nhãn bước hiển thị trong log. Để trống sẽ dùng tên method.
     */
    String value() default "";
}
