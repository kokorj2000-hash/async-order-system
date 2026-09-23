package com.aops.common.event;

import com.aops.common.model.OrderStatus;

import java.math.BigDecimal;

public class OrderCreatedEvent {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private OrderStatus status;

    public  OrderCreatedEvent() {}

    public  OrderCreatedEvent(Long id, Long userId, BigDecimal amount, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {return userId;}

    public void setUserId(Long userId) {this.userId = userId;}

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}