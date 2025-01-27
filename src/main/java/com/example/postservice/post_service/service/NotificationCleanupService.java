package com.example.postservice.post_service.service;

import com.example.postservice.post_service.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationCleanupService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByTimestampBefore(cutoffDate);
    }
}
