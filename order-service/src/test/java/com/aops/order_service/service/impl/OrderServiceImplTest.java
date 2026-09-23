
package com.aops.order_service.service.impl;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.model.OrderStatus;
import com.aops.common.security.CurrentUser;
import com.aops.order_service.dto.request.CreateOrderRequest;
import com.aops.order_service.dto.response.OrderResponse;
import com.aops.order_service.mapper.OrderMapper;
import com.aops.order_service.model.Order;
import com.aops.order_service.producer.OrderProducer;
import com.aops.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void shouldCreateOrderAndSendEvent() {

        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAmount(BigDecimal.valueOf(1200));

        Order order = new Order();
        order.setAmount(BigDecimal.valueOf(1200));
        order.setStatus(OrderStatus.CREATED);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(10L);
        savedOrder.setAmount(BigDecimal.valueOf(1200));
        savedOrder.setStatus(OrderStatus.CREATED);

        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setUserId(10L);
        response.setAmount(BigDecimal.valueOf(1200));
        response.setStatus(OrderStatus.CREATED);

        when(orderMapper.toEntity(request)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(response);

        try (MockedStatic<CurrentUser> currentUser = mockStatic(CurrentUser.class)) {

            currentUser.when(CurrentUser::getId).thenReturn(10L);

            // Act
            OrderResponse result = orderService.createOrder(request);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(10L, result.getUserId());
            assertEquals(BigDecimal.valueOf(1200), result.getAmount());
            assertEquals(OrderStatus.CREATED, result.getStatus());

            verify(orderRepository).save(order);

            ArgumentCaptor<OrderCreatedEvent> eventCaptor =
                    ArgumentCaptor.forClass(OrderCreatedEvent.class);

            verify(orderProducer).sendOrderCreatedEvent(eventCaptor.capture());

            OrderCreatedEvent capturedEvent = eventCaptor.getValue();

            assertEquals(10L, capturedEvent.getUserId());
            assertEquals(BigDecimal.valueOf(1200), capturedEvent.getAmount());
            assertEquals(OrderStatus.CREATED, capturedEvent.getStatus());
        }
    }
}