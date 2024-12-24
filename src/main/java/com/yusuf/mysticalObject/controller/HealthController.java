package com.yusuf.mysticalObject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class HealthController {
    
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    @GetMapping("/actuator/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Health check endpoint called");
        return ResponseEntity.ok("UP");
    }
} 