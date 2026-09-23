package com.aops.notification_service.service.impl;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.event.OrderPaidEvent;
import com.aops.common.security.CurrentUser;
import com.aops.notification_service.dto.response.NotificationResponse;
import com.aops.notification_service.exception.NotificationNotFoundException;
import com.aops.notification_service.mapper.NotificationMapper;
import com.aops.notification_service.model.Notification;
import com.aops.notification_service.repository.NotificationRepository;
import com.aops.notification_service.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public void createNotification(OrderCreatedEvent event) {

        Notification notification = new Notification();

        notification.setOrderId(event.getId());
        notification.setUserId(event.getUserId());
        notification.setMessage("Order № " + event.getId() + " was created!");

        notificationRepository.save(notification);
    }

    @Override
    public void createNotification(OrderPaidEvent event) {

        Notification notification = new Notification();

        notification.setOrderId(event.getOrderId());
        notification.setUserId(event.getUserId());
        notification.setMessage("Order № " + event.getOrderId() + " was paid");

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getAllNotifications() {

        List<Notification> notifications;

        if (CurrentUser.isAdmin()) {
            notifications = notificationRepository.findAll();
        } else {
            notifications = notificationRepository.findByUserId(CurrentUser.getId());
        }

        return notifications.stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    public NotificationResponse getNotificationById(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));

        if (!CurrentUser.isAdmin()
                && !notification.getUserId().equals(CurrentUser.getId())) {

            throw new AccessDeniedException("Access denied");
        }

        return notificationMapper.toResponse(notification);
    }
}
