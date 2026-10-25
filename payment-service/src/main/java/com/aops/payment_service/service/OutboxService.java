package com.aops.payment_service.service;

import com.aops.payment_service.model.OutboxEvent;
import com.aops.payment_service.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxRepository outboxRepository,
                         ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void save(String topic, String key, Object event) {

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setTopic(topic);
        outboxEvent.setEventKey(key);
        outboxEvent.setEventType(event.getClass().getName());

        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize event " + event.getClass().getSimpleName(), e);
        }

        outboxRepository.save(outboxEvent);
    }
}