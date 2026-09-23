package com.aops.order_service.consumer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderPaidEvent;
import com.aops.order_service.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_COMPLETED,
            groupId = "order-group"
    )
    public void consumePaymentService(OrderPaidEvent event) {
        orderService.updateOrderStatusByOrderId(event.getOrderId());
    }
}