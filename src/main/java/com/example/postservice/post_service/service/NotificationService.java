package com.example.postservice.post_service.service;

import com.example.postservice.post_service.dto.NotificationDTO;
import com.example.postservice.post_service.entity.Notification;
import com.example.postservice.post_service.entity.User;
import com.example.postservice.post_service.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendWebSocketNotification(String message, String type, Long userId) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, new NotificationDTO(message, type, userId));
    }

    // Fetch unread notifications for the logged-in user
    public List<Notification> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findByUserIdAndReadFalseOrderByTimestampDesc(userId);
    }

    // Fetch all notifications for the logged-in user
    public List<Notification> getAllNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByTimestampDesc(userId);
    }

    // Mark a notification as read
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // Create a new notification
    public Notification createNotification(String message, String type, Long userId) {
        Notification notification = new Notification();
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setTimestamp(LocalDateTime.now());
        notification.setUser(userService.findUserById(userId)); // Fetch the user
        return notificationRepository.save(notification);
    }

    // Utility method to get the logged-in user's ID
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByUsername(username).getId();
    }

    public void createGlobalNotification(String message, String type) {
        List<User> users = userService.getAllUsers(); // Fetch all users
        for (User user : users) {
            createNotification(message, type, user.getId());
        }
    }
}