package com.zlatenov.wedding_backend.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.zlatenov.wedding_backend.exception.TooManyRequestsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class RateLimiterService {
    private final LoadingCache<String, AtomicInteger> requestCounts;

    public RateLimiterService() {
        this.requestCounts = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public AtomicInteger load(String key) {
                        return new AtomicInteger(0);
                    }
                });
    }

    public void checkRateLimit(String key, int maxRequests) {
        AtomicInteger counter = requestCounts.getUnchecked(key);
        int currentCount = counter.incrementAndGet();

        if (currentCount > maxRequests) {
            log.warn("Rate limit exceeded for key: {}", key);
            throw new TooManyRequestsException("Too many requests. Please try again later.");
        }

        log.debug("Request {} of {} for key: {}", currentCount, maxRequests, key);
    }

    public void clearRateLimit(String key) {
        requestCounts.invalidate(key);
    }
}