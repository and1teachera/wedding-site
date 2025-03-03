package com.zlatenov.wedding_backend.logging;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTraceFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String CLIENT_IP = "clientIp";
    private static final String REQUEST_DURATION = "requestDuration";
    private static final String STATUS_CODE = "statusCode";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Generate or extract trace and request IDs
        String traceId = UUID.randomUUID().toString();
        String requestId = request.getHeader("X-Request-ID");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Add IDs to MDC for logging
        MDC.put(TRACE_ID, traceId);
        MDC.put(REQUEST_ID, requestId);
        
        // Extract client IP
        String clientIp = extractClientIp(request);
        MDC.put(CLIENT_IP, clientIp);
        
        // Extract authenticated user if available
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            MDC.put(USER_ID, authentication.getName());
        }
        
        // Add trace ID to response headers for frontend correlation
        response.setHeader("X-Trace-ID", traceId);
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put(REQUEST_DURATION, String.valueOf(duration));
            MDC.put(STATUS_CODE, String.valueOf(response.getStatus()));
            
            log.info("Response: {} {} - status: {}, duration: {}ms", 
                    request.getMethod(), request.getRequestURI(), 
                    response.getStatus(), duration);
            
            MDC.clear();
        }
    }
    
    private String extractClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        
        // Extract first IP if multiple in X-Forwarded-For
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }
        
        return clientIp;
    }
}