package com.aops.notification_service.repository;

import com.aops.notification_service.model.Notification;
import com.aops.notification_service.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    boolean existsByOrderIdAndType(Long orderId, NotificationType type);

}