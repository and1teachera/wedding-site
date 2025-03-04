package com.zlatenov.wedding_backend.controller;

import com.zlatenov.wedding_backend.dto.FrontendLogRequest;
import com.zlatenov.wedding_backend.service.FrontendLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling frontend log submissions
 */
@Slf4j
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {
    
    private final FrontendLogService frontendLogService;
    
    /**
     * Endpoint for processing frontend logs
     *
     * @param request The frontend log request containing log entries and client info
     * @return Response with status 200 if logs were processed successfully
     */
    @PostMapping
    public ResponseEntity<Void> receiveLogs(@Valid @RequestBody FrontendLogRequest request) {
        log.debug("Received {} log entries from frontend", request.getLogs().size());
        frontendLogService.processFrontendLogs(request);
        return ResponseEntity.ok().build();
    }
}