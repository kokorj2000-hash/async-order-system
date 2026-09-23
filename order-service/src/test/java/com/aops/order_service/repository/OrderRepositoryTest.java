package com.aops.order_service.repository;

import com.aops.common.model.OrderStatus;
import com.aops.order_service.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
class OrderRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldSaveOrder() {

        // Arrange
        Order order = createOrder(10L, BigDecimal.valueOf(1500));

        // Act
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals(10L, savedOrder.getUserId());
        assertEquals(BigDecimal.valueOf(1500), savedOrder.getAmount());
        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
    }

    @Test
    void shouldFindOrderById() {

        // Arrange
        Order savedOrder = orderRepository.save(
                createOrder(10L, BigDecimal.valueOf(1500))
        );

        // Act
        Optional<Order> found = orderRepository.findById(savedOrder.getId());


        // Assert
        Order foundOrder = found.orElseThrow();

        assertEquals(10L, foundOrder.getUserId());
        assertEquals(BigDecimal.valueOf(1500), foundOrder.getAmount());
        assertEquals(OrderStatus.CREATED, foundOrder.getStatus());
    }

    @Test
    void shouldFindOrdersByUserId() {

        // Arrange
        orderRepository.save(createOrder(10L, BigDecimal.valueOf(1500)));
        orderRepository.save(createOrder(10L, BigDecimal.valueOf(2500)));
        orderRepository.save(createOrder(20L, BigDecimal.valueOf(500)));

        // Act
        List<Order> orders = orderRepository.findByUserId(10L);

        // Assert
        assertEquals(2, orders.size());

        assertTrue(
                orders.stream()
                        .allMatch(order -> order.getUserId().equals(10L))
        );
    }

    @Test
    void shouldReturnEmptyListForUnknownUser() {

        // Arrange
        orderRepository.save(createOrder(10L, BigDecimal.valueOf(1500)));

        // Act
        List<Order> orders = orderRepository.findByUserId(999L);

        // Assert
        assertNotNull(orders);
        assertTrue(orders.isEmpty());
    }

    private Order createOrder(Long userId, BigDecimal amount) {

        Order order = new Order();

        order.setUserId(userId);
        order.setAmount(amount);
        order.setStatus(OrderStatus.CREATED);

        return order;
    }
}