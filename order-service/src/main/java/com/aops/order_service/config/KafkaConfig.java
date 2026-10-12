package com.aops.order_service.config;

import com.aops.common.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(KafkaTopics.ORDER_CREATED, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return new NewTopic(KafkaTopics.PAYMENT_COMPLETED, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentCompletedDlt() {
        return new NewTopic(KafkaTopics.PAYMENT_COMPLETED + ".DLT", 1, (short) 1);
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate);

        ExponentialBackOff backOff = new ExponentialBackOff(1_000L, 2.0);
        backOff.setMaxInterval(10_000L);
        backOff.setMaxElapsedTime(30_000L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}