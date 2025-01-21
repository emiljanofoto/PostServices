package com.example.postservice.post_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
@Data
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String priority; // New field: Standard, Express, Overnight

    @Lob
    private byte[] barcode;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
}