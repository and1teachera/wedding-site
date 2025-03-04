package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FrontendLogEntry;
import com.zlatenov.wedding_backend.dto.FrontendLogRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

/**
 * Service for processing frontend logs
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FrontendLogService {
    
    private static final Logger frontendLogger = LoggerFactory.getLogger("frontend");
    private static final Logger frontendErrorLogger = LoggerFactory.getLogger("frontend.error");
    
    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String CLIENT_URL = "clientUrl";
    private static final String CLIENT_UA = "clientUserAgent";
    private static final String SOURCE = "source";
    
    /**
     * Process logs received from the frontend
     *
     * @param request The frontend log request containing log entries and client info
     */
    public void processFrontendLogs(FrontendLogRequest request) {
        try {
            setupMdcContext(request);
            
            log.info("Processing {} frontend log entries from URL: {}", 
                    request.getLogs().size(), 
                    request.getClientInfo().getUrl());
            
            for (FrontendLogEntry entry : request.getLogs()) {
                processLogEntry(entry);
            }
        } finally {
            MDC.clear();
        }
    }
    
    /**
     * Set up MDC context with correlation IDs and client information
     *
     * @param request The frontend log request
     */
    private void setupMdcContext(FrontendLogRequest request) {
        FrontendLogEntry firstEntry = !request.getLogs().isEmpty() ? request.getLogs().get(0) : null;
        
        if (firstEntry != null && firstEntry.getTraceId() != null) {
            MDC.put(TRACE_ID, firstEntry.getTraceId());
        }
        
        if (firstEntry != null && firstEntry.getRequestId() != null) {
            MDC.put(REQUEST_ID, firstEntry.getRequestId());
        }
        
        if (firstEntry != null && firstEntry.getUserId() != null) {
            MDC.put(USER_ID, firstEntry.getUserId());
        }
        
        MDC.put(CLIENT_URL, request.getClientInfo().getUrl());
        MDC.put(CLIENT_UA, request.getClientInfo().getUserAgent());
        MDC.put(SOURCE, "frontend");
    }
    
    /**
     * Process an individual log entry by routing it to the appropriate logger
     *
     * @param entry The log entry to process
     */
    private void processLogEntry(FrontendLogEntry entry) {
        // Set entry-specific MDC values
        if (entry.getTraceId() != null) {
            MDC.put(TRACE_ID, entry.getTraceId());
        }
        
        if (entry.getRequestId() != null) {
            MDC.put(REQUEST_ID, entry.getRequestId());
        }
        
        if (entry.getUserId() != null) {
            MDC.put(USER_ID, entry.getUserId());
        }
        
        String logPrefix = entry.getContext() != null ? "[" + entry.getContext() + "] " : "";
        String logMessage = logPrefix + entry.getMessage();
        
        // Get appropriate logger based on log level
        Logger targetLogger = shouldUseErrorLogger(entry.getLevel()) ? frontendErrorLogger : frontendLogger;
        
        // Log at appropriate level
        switch (entry.getLevel()) {
            case 0: // TRACE
                targetLogger.trace(logMessage);
                break;
            case 1: // DEBUG
                targetLogger.debug(logMessage);
                break;
            case 2: // INFO
                targetLogger.info(logMessage);
                break;
            case 3: // WARN
                targetLogger.warn(logMessage);
                break;
            case 4: // ERROR
            case 5: // FATAL
                if (entry.getStack() != null) {
                    targetLogger.error("{}. Stack trace: {}", logMessage, entry.getStack());
                } else {
                    targetLogger.error(logMessage);
                }
                break;
            default:
                targetLogger.info(logMessage);
                break;
        }
    }
    
    /**
     * Determine if the error logger should be used based on log level
     *
     * @param level The log level
     * @return true if the error logger should be used
     */
    private boolean shouldUseErrorLogger(int level) {
        // Use error logger for ERROR (4) and FATAL (5) levels
        return level >= 4;
    }
}