package com.chemistry.demo.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to disable AOP logging for specific methods or classes.
 * Useful for methods dealing with huge binary data or highly sensitive custom
 * processing.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLogging {
}
