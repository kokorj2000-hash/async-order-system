package com.aops.common.event;

public class OrderPaidEvent {

    private Long orderId;

    private Long userId;

    public OrderPaidEvent() {}

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}