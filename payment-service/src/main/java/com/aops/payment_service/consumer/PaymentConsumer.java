package com.aops.payment_service.consumer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderCreatedEvent;
import com.aops.payment_service.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentConsumer {

    private final PaymentService paymentService;

    public PaymentConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "payment-group"
    )
    public void consumeOrderService(OrderCreatedEvent event) {

        log.info(
                "OrderCreatedEvent received. orderId={}, userId={}",
                event.getId(),
                event.getUserId()
        );

        paymentService.createPayment(event);

        log.info(
                "Payment created from OrderCreatedEvent. orderId={}, userId={}",
                event.getId(),
                event.getUserId()
        );
    }
}