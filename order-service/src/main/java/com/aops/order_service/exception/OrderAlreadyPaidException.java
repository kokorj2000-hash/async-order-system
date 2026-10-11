package com.aops.order_service.exception;

public class OrderAlreadyPaidException extends RuntimeException {

    public OrderAlreadyPaidException(Long id) {
        super("Order with id " + id + " is already paid and cannot be deleted");
    }

}