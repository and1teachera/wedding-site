package com.zlatenov.wedding_backend.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    @Around("@annotation(logPerformance)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, LogPerformance logPerformance) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String description = logPerformance.description().isEmpty() ? 
                methodName : logPerformance.description();
        
        long startTime = System.currentTimeMillis();
        
        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Performance: {}.{} - {} completed in {}ms", 
                    className, methodName, description, executionTime);
            
            // Flag slow executions
            if (executionTime > 500) {
                log.warn("Slow execution detected: {}.{} took {}ms", 
                        className, methodName, executionTime);
            }
        }
    }
}