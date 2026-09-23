package com.aops.notification_service.service;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.event.OrderPaidEvent;
import com.aops.notification_service.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    void createNotification(OrderCreatedEvent event);

    void createNotification(OrderPaidEvent event);

    List<NotificationResponse> getAllNotifications();

    NotificationResponse getNotificationById(Long id);

}
