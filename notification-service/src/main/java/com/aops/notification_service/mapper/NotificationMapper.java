package com.aops.notification_service.mapper;

import com.aops.notification_service.dto.response.NotificationResponse;
import com.aops.notification_service.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification){

        NotificationResponse response = new NotificationResponse();

        response.setId(notification.getId());
        response.setOrderId(notification.getOrderId());
        response.setUserId(notification.getUserId());
        response.setMessage(notification.getMessage());

        return response;
    }

}
