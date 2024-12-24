package com.yusuf.mysticalObject.controller;

import com.yusuf.mysticalObject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/db")
    public ResponseEntity<String> checkDb() {
        try {
            userRepository.count(); // Simple DB operation
            return ResponseEntity.ok("Database connection successful");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Database connection failed: " + e.getMessage());
        }
    }
} 