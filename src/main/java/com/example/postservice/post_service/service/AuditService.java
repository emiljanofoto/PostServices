package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.AuditLog;
import com.example.postservice.post_service.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(String action, String performedBy, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}