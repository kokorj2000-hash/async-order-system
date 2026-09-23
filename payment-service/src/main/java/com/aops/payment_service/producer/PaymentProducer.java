package com.aops.payment_service.producer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderPaidEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private final KafkaTemplate<String, OrderPaidEvent> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, OrderPaidEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentPayEvent(OrderPaidEvent event) {
        kafkaTemplate.send(KafkaTopics.PAYMENT_COMPLETED, event);
    }

}