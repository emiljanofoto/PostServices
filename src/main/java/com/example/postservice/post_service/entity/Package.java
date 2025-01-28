package com.example.postservice.post_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

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
    private String senderAddress;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String recipientAddress;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String priority; // New field: Standard, Express, Overnight

    @Lob
    private byte[] barcode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredDate; // Date when the package was delivered

    @PrePersist
    protected void onCreate() {
        if (this.createdDate == null) {
            this.createdDate = new Date(); // Set to current timestamp
        }
    }

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    private String recipientPhoneNumber;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transitTimestamp;

}