package com.chemistry.demo.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Đánh dấu một method nghiệp vụ cần ghi audit lên ELK (thanh toán, kích hoạt
 * kit, cấp quyền, thưởng AR...). {@link AuditEventAspect} sẽ emit một
 * {@code PerformanceLog} với eventType = {@link #value()} kèm actor hiện tại.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditEvent {

    /**
     * Tên action nghiệp vụ, dùng làm eventType để filter trên Kibana.
     * Ví dụ: PAYMENT_VERIFIED, KIT_ACTIVATED, PACKAGE_GRANTED, AR_REWARD_CLAIMED.
     */
    String value();
}
