package com.aops.order_service.producer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderProducer.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {

        log.info(
                "Sending OrderCreatedEvent to Kafka. topic={}, orderId={}, userId={}",
                KafkaTopics.ORDER_CREATED,
                event.getId(),
                event.getUserId()
        );

        kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {

                log.error(
                        "Failed to send OrderCreatedEvent. orderId={}, userId={}",
                        event.getId(),
                        event.getUserId(),
                        exception
                );

                return;
            }

            log.info(
                    "OrderCreatedEvent successfully sent to Kafka. topic={}, orderId={}",
                    KafkaTopics.ORDER_CREATED,
                    event.getId()
            );
        });
    }
}