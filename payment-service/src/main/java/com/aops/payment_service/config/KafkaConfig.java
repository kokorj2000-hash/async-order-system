package com.aops.payment_service.config;

import com.aops.common.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}