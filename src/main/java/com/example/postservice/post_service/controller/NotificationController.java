package com.example.postservice.post_service.controller;

import com.example.postservice.post_service.dto.NotificationDTO;
import com.example.postservice.post_service.entity.Notification;
import com.example.postservice.post_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get all notifications for the logged-in user
    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    // Get unread notifications for the logged-in user
    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }

    // Mark a notification as read
    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    // Endpoint to create a notification (for testing purposes)
    @PostMapping
    public Notification createNotification(@RequestBody NotificationDTO notificationDTO) {
        return notificationService.createNotification(
                notificationDTO.getMessage(),
                notificationDTO.getType(),
                notificationDTO.getUserId()
        );
    }


}
