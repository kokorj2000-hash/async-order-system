package com.aops.order_service.service;

import com.aops.order_service.dto.request.CreateOrderRequest;
import com.aops.order_service.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    OrderResponse createOrder(CreateOrderRequest request);

    void updateOrderStatusByOrderId(Long id);

    void deleteOrderById(Long id);
}