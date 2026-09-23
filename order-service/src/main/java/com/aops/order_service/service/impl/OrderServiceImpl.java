package com.aops.order_service.service.impl;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.model.OrderStatus;
import com.aops.common.security.CurrentUser;
import com.aops.order_service.dto.request.CreateOrderRequest;
import com.aops.order_service.dto.response.OrderResponse;
import com.aops.order_service.exception.OrderNotFoundException;
import com.aops.order_service.mapper.OrderMapper;
import com.aops.order_service.model.Order;
import com.aops.order_service.producer.OrderProducer;
import com.aops.order_service.repository.OrderRepository;
import com.aops.order_service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderProducer orderProducer;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderProducer = orderProducer;
    }

    @Override
    public List<OrderResponse> getAllOrders() {

        Long userId = CurrentUser.getId();

        if (CurrentUser.isAdmin()) {

            log.info("Admin requested all orders. userId={}", userId);

            return orderRepository.findAll()
                    .stream()
                    .map(orderMapper::toResponse)
                    .toList();
        }

        log.info("User requested own orders. userId={}", userId);

        return orderRepository.findByUserId(userId)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long id) {

        log.info("Request to get order. orderId={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found. orderId={}", id);
                    return new OrderNotFoundException(id);
                });

        if (!CurrentUser.isAdmin()
                && !order.getUserId().equals(CurrentUser.getId())) {

            log.warn(
                    "Access denied for order. orderId={}, userId={}",
                    id,
                    CurrentUser.getId()
            );

            throw new AccessDeniedException("Access denied");
        }

        log.info("Order successfully retrieved. orderId={}", id);

        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        Long userId = CurrentUser.getId();

        log.info(
                "Creating order. userId={}, amount={}",
                userId,
                request.getAmount()
        );

        Order order = orderMapper.toEntity(request);

        order.setUserId(userId);

        Order savedOrder = orderRepository.save(order);

        log.info(
                "Order saved to database. orderId={}, userId={}",
                savedOrder.getId(),
                savedOrder.getUserId()
        );

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getAmount(),
                savedOrder.getStatus()
        );

        orderProducer.sendOrderCreatedEvent(event);

        log.info(
                "Order created successfully. orderId={}, userId={}",
                savedOrder.getId(),
                savedOrder.getUserId()
        );

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public void updateOrderStatusByOrderId(Long id) {

        log.info("Updating order status. orderId={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found for status update. orderId={}", id);
                    return new OrderNotFoundException(id);
                });

        order.setStatus(OrderStatus.PAID);

        orderRepository.save(order);

        log.info(
                "Order status updated to PAID. orderId={}",
                id
        );
    }

    @Override
    public void deleteOrderById(Long id) {

        log.info("Request to delete order. orderId={}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found for deletion. orderId={}", id);
                    return new OrderNotFoundException(id);
                });

        if (!CurrentUser.isAdmin()
                && !order.getUserId().equals(CurrentUser.getId())) {

            log.warn(
                    "Access denied for order deletion. orderId={}, userId={}",
                    id,
                    CurrentUser.getId()
            );

            throw new AccessDeniedException("Access denied");
        }

        orderRepository.deleteById(id);

        log.info("Order deleted successfully. orderId={}", id);
    }
}