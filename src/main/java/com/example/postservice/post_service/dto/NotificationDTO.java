package com.example.postservice.post_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String message; // Notification message
    private String type;    // Notification type (e.g., "package-status", "task")
    private Long userId;
}
