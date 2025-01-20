package com.example.postservice.post_service.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private String status; // e.g., "In Transit", "Delivered", etc.

    @Lob
    private byte[] barcode;
}