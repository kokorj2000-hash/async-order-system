package com.aops.order_service.mapper;

import com.aops.common.model.OrderStatus;
import com.aops.order_service.dto.request.CreateOrderRequest;
import com.aops.order_service.dto.response.OrderResponse;
import com.aops.order_service.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public Order toEntity(CreateOrderRequest request) {

        Order order = new Order();

        order.setAmount(request.getAmount());

        order.setStatus(OrderStatus.CREATED);

        return order;
    }

    public OrderResponse toResponse(Order order) {

        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setAmount(order.getAmount());
        response.setStatus(order.getStatus());

        return response;
    }

}