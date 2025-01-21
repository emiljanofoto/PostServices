package com.example.postservice.post_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.sql.Connection;

@RestController
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        StringBuilder healthStatus = new StringBuilder();
        boolean isHealthy = true;

        // Check Database Connectivity
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            if (!connection.isValid(1)) {
                isHealthy = false;
            }
            healthStatus.append("Database: OK\n");
        } catch (Exception e) {
            isHealthy = false;
            healthStatus.append("Database: UNAVAILABLE\n");
        }

        // Add Uptime Information
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        healthStatus.append("Uptime: ").append(uptime / 1000).append(" seconds\n");

        if (isHealthy) {
            return ResponseEntity.ok("Status: OK\n" + healthStatus);
        } else {
            return ResponseEntity.status(503).body("Status: UNAVAILABLE\n" + healthStatus);
        }
    }
    }