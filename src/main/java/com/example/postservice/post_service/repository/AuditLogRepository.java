package com.example.postservice.post_service.repository;

import com.example.postservice.post_service.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}