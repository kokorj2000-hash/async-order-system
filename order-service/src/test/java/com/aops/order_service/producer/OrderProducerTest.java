package com.aops.order_service.producer;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderProducerTest {

    @Mock
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    @Test
    void shouldSendOrderCreatedEvent() {

        // Arrange
        OrderCreatedEvent event = new OrderCreatedEvent(
                1L,
                10L,
                BigDecimal.valueOf(1500),
                OrderStatus.CREATED
        );

        // Act
        orderProducer.sendOrderCreatedEvent(event);

        // Assert
        ArgumentCaptor<OrderCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(OrderCreatedEvent.class);

        verify(kafkaTemplate).send(
                org.mockito.ArgumentMatchers.anyString(),
                eventCaptor.capture()
        );

        OrderCreatedEvent sentEvent = eventCaptor.getValue();

        assertEquals(1L, sentEvent.getId());
        assertEquals(10L, sentEvent.getUserId());
        assertEquals(BigDecimal.valueOf(1500), sentEvent.getAmount());
        assertEquals(OrderStatus.CREATED, sentEvent.getStatus());
    }
}