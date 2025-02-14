package com.zlatenov.wedding_backend.aspect;

import com.zlatenov.wedding_backend.annotation.RateLimited;
import com.zlatenov.wedding_backend.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RateLimiterService rateLimiterService;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Around("@annotation(rateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimited rateLimited) throws Throwable {
        String key = getKey(rateLimited);
        rateLimiterService.checkRateLimit(key, rateLimited.requests());

        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // In case of error, we might want to handle the rate limit counter differently
            log.error("Error during rate-limited operation", throwable);
            throw throwable;
        }
    }

    private String getKey(RateLimited rateLimited) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String ip = request.getRemoteAddr();

        if (rateLimited.key().isEmpty()) {
            return String.format("%s_%s", request.getRequestURI(), ip);
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("ip", ip);
        context.setVariable("request", request);

        return expressionParser.parseExpression(rateLimited.key())
                .getValue(context, String.class);
    }
}