package com.example.postservice.post_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, etc.

    @Column(nullable = false)
    private String performedBy; // Username or User ID

    @Column(nullable = false)
    private LocalDateTime timestamp; // When the action occurred

    @Lob
    private String details; // Additional details (e.g., package ID or tracking number)
}