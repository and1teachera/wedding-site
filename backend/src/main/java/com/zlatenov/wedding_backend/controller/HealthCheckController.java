package com.zlatenov.wedding_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Angel Zlatenov
 */
@RestController
public class HealthCheckController {
    @GetMapping("/")
    public String home() {
        return "The Wedding Backend is Running!";
    }
}

