package com.aops.notification_service.service.impl;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.event.OrderPaidEvent;
import com.aops.common.security.CurrentUser;
import com.aops.notification_service.dto.response.NotificationResponse;
import com.aops.notification_service.exception.NotificationNotFoundException;
import com.aops.notification_service.mapper.NotificationMapper;
import com.aops.notification_service.model.Notification;
import com.aops.notification_service.model.NotificationType;
import com.aops.notification_service.repository.NotificationRepository;
import com.aops.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public void createNotification(OrderCreatedEvent event) {

        if (notificationRepository.existsByOrderIdAndType(event.getId(), NotificationType.ORDER_CREATED)) {
            log.info("ORDER_CREATED notification for orderId={} already exists, skipping", event.getId());
            return;
        }

        Notification notification = new Notification();
        notification.setOrderId(event.getId());
        notification.setUserId(event.getUserId());
        notification.setMessage("Order № " + event.getId() + " was created!");
        notification.setType(NotificationType.ORDER_CREATED);

        try {
            notificationRepository.save(notification);
        } catch (DataIntegrityViolationException e) {
            log.info("Concurrent duplicate ORDER_CREATED notification ignored for orderId={}", event.getId());
        }
    }

    @Override
    public void createNotification(OrderPaidEvent event) {

        if (notificationRepository.existsByOrderIdAndType(event.getOrderId(), NotificationType.ORDER_PAID)) {
            log.info("ORDER_PAID notification for orderId={} already exists, skipping", event.getOrderId());
            return;
        }

        Notification notification = new Notification();
        notification.setOrderId(event.getOrderId());
        notification.setUserId(event.getUserId());
        notification.setMessage("Order № " + event.getOrderId() + " was paid");
        notification.setType(NotificationType.ORDER_PAID);

        try {
            notificationRepository.save(notification);
        } catch (DataIntegrityViolationException e) {
            log.info("Concurrent duplicate ORDER_PAID notification ignored for orderId={}", event.getOrderId());
        }
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
