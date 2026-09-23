package com.aops.payment_service.mapper;

import com.aops.payment_service.dto.response.PaymentResponse;
import com.aops.payment_service.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment){

        PaymentResponse response = new PaymentResponse();

        response.setId(payment.getId());
        response.setUserId(payment.getUserId());
        response.setOrderId(payment.getOrderId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());

        return response;
    }

}
