package com.zlatenov.wedding_backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Angel Zlatenov
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimited {
    /**
     * Number of requests allowed per time window
     */
    int requests() default 5;

    /**
     * Time window in seconds
     */
    int timeWindowSeconds() default 60;

    /**
     * Key to identify the rate limit bucket.
     * Supports SpEL expressions.
     */
    String key() default "";
}