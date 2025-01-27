package com.example.postservice.post_service.repository;

import com.example.postservice.post_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch unread notifications for a user
    List<Notification> findByUserIdAndReadFalseOrderByTimestampDesc(Long userId);

    // Fetch all notifications for a user
    List<Notification> findByUserIdOrderByTimestampDesc(Long userId);

    void deleteByTimestampBefore(LocalDateTime cutoffDate);
}
