package com.aops.payment_service.service;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.payment_service.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {

    void createPayment(OrderCreatedEvent event);

    PaymentResponse payOrderByPaymentId(Long id);

    PaymentResponse getPaymentById(Long id);

    List<PaymentResponse> getAllPayments();

}
