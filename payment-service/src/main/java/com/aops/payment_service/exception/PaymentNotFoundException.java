package com.aops.payment_service.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Payment with id " + id + " not found");
    }
}
