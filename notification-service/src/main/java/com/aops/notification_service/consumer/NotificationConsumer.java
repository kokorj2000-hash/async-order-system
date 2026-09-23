package com.aops.notification_service.consumer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.event.OrderPaidEvent;
import com.aops.notification_service.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    private final NotificationService notificationService;

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "notification-group"
    )
    public void consumeOrderService(OrderCreatedEvent event) {

        log.info(
                "OrderCreatedEvent received. orderId={}, userId={}",
                event.getId(),
                event.getUserId()
        );

        notificationService.createNotification(event);

        log.info(
                "Notification created for new order. orderId={}, userId={}",
                event.getId(),
                event.getUserId()
        );
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = "notification-group"
    )
    public void consumePaymentService(OrderPaidEvent event) {

        log.info(
                "OrderPaidEvent received. orderId={}, userId={}",
                event.getOrderId(),
                event.getUserId()
        );

        notificationService.createNotification(event);

        log.info(
                "Payment notification created. orderId={}, userId={}",
                event.getOrderId(),
                event.getUserId()
        );
    }
}