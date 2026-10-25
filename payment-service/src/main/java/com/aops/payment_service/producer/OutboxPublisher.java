package com.aops.payment_service.producer;

import com.aops.payment_service.model.OutboxEvent;
import com.aops.payment_service.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxRepository outboxRepository,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publish() {

        for (OutboxEvent outbox : outboxRepository
                .findByPublishedAtIsNullOrderByIdAsc(PageRequest.ofSize(50))) {
            try {
                Object event = objectMapper.readValue(
                        outbox.getPayload(),
                        Class.forName(outbox.getEventType()));

                kafkaTemplate
                        .send(outbox.getTopic(), outbox.getEventKey(), event)
                        .get(5, TimeUnit.SECONDS);

                outbox.setPublishedAt(LocalDateTime.now());

            } catch (Exception e) {
                log.error("Outbox publish failed, will retry. outboxId={}",
                        outbox.getId(), e);
                return;
            }
        }
    }
}