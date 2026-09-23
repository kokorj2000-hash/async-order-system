package com.aops.payment_service.consumer;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.model.OrderStatus;
import com.aops.payment_service.model.Payment;
import com.aops.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.properties.spring.json.trusted.packages=*",
        "eureka.client.enabled=false",
        "jwt.secret=test-secret-key-for-tests-12345678901234567890"
})
@EmbeddedKafka(
        partitions = 1,
        topics = KafkaTopics.ORDER_CREATED
)
@Testcontainers
class PaymentConsumerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldCreatePaymentWhenOrderCreatedEventReceived() {

        // Arrange
        OrderCreatedEvent event = new OrderCreatedEvent(
                1L,
                10L,
                BigDecimal.valueOf(1500),
                OrderStatus.CREATED
        );

        // Act
        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event);

        // Assert
        org.awaitility.core.ConditionFactory await = await();
        await.atMost(Duration.ofSeconds(5));
        await.untilAsserted(() -> {

            Payment payment = paymentRepository
                    .findByOrderId(1L)
                    .orElseThrow();

            assertEquals(10L, payment.getUserId());
            assertTrue(BigDecimal.valueOf(1500).compareTo(payment.getAmount()) == 0);        });
    }
}