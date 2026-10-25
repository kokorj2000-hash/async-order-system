package com.aops.order_service.producer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.model.OrderStatus;
import com.aops.order_service.model.OutboxEvent;
import com.aops.order_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private OutboxPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new OutboxPublisher(outboxRepository, kafkaTemplate, objectMapper);
    }

    private OutboxEvent outboxEvent(long id) throws Exception {
        OrderCreatedEvent event = new OrderCreatedEvent(
                id, 10L, BigDecimal.valueOf(1500), OrderStatus.CREATED);

        OutboxEvent outbox = new OutboxEvent();
        outbox.setId(id);
        outbox.setTopic(KafkaTopics.ORDER_CREATED);
        outbox.setEventKey(String.valueOf(id));
        outbox.setEventType(OrderCreatedEvent.class.getName());
        outbox.setPayload(objectMapper.writeValueAsString(event));
        return outbox;
    }

    @Test
    void shouldMarkEventAsPublishedAfterSuccessfulSend() throws Exception {

        OutboxEvent outbox = outboxEvent(1L);

        when(outboxRepository.findByPublishedAtIsNullOrderByIdAsc(any(Pageable.class)))
                .thenReturn(List.of(outbox));
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.<SendResult<String, Object>>completedFuture(null));

        publisher.publish();

        assertNotNull(outbox.getPublishedAt());
        verify(kafkaTemplate).send(
                eq(KafkaTopics.ORDER_CREATED), eq("1"), any(OrderCreatedEvent.class));
    }

    @Test
    void shouldKeepEventUnpublishedWhenKafkaFails() throws Exception {

        OutboxEvent outbox = outboxEvent(1L);

        when(outboxRepository.findByPublishedAtIsNullOrderByIdAsc(any(Pageable.class)))
                .thenReturn(List.of(outbox));
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.<SendResult<String, Object>>failedFuture(
                        new RuntimeException("Kafka is down")));

        publisher.publish();

        assertNull(outbox.getPublishedAt());
    }

    @Test
    void shouldStopOnFirstFailureToPreserveOrder() throws Exception {

        OutboxEvent first = outboxEvent(1L);
        OutboxEvent second = outboxEvent(2L);

        when(outboxRepository.findByPublishedAtIsNullOrderByIdAsc(any(Pageable.class)))
                .thenReturn(List.of(first, second));
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.<SendResult<String, Object>>failedFuture(
                        new RuntimeException("Kafka is down")));

        publisher.publish();

        assertNull(first.getPublishedAt());
        assertNull(second.getPublishedAt());
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any());
    }
}