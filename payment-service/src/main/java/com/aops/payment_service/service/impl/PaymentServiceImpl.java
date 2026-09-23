package com.aops.payment_service.service.impl;

import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.event.OrderPaidEvent;
import com.aops.common.model.OrderStatus;
import com.aops.common.security.CurrentUser;
import com.aops.payment_service.dto.response.PaymentResponse;
import com.aops.payment_service.exception.PaymentNotFoundException;
import com.aops.payment_service.mapper.PaymentMapper;
import com.aops.payment_service.model.Payment;
import com.aops.payment_service.producer.PaymentProducer;
import com.aops.payment_service.repository.PaymentRepository;
import com.aops.payment_service.service.PaymentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentProducer paymentProducer;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              PaymentProducer paymentProducer) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentProducer = paymentProducer;
    }

    @Override
    public void createPayment(OrderCreatedEvent event) {

        Payment payment = new Payment();

        payment.setOrderId(event.getId());
        payment.setUserId(event.getUserId());
        payment.setAmount(event.getAmount());
        payment.setStatus(event.getStatus());

        paymentRepository.save(payment);
    }

    @Override
    public PaymentResponse payOrderByPaymentId(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (!payment.getUserId().equals(CurrentUser.getId())) {

            throw new AccessDeniedException("Access denied");
        }

        payment.setStatus(OrderStatus.PAID);

        OrderPaidEvent event = new OrderPaidEvent();

        event.setOrderId(payment.getOrderId());

        event.setUserId(payment.getUserId());

        paymentProducer.sendPaymentPayEvent(event);

        paymentRepository.save(payment);

        return paymentMapper.toResponse(payment);
    }

    public List<PaymentResponse> getAllPayments() {

        List<Payment> payments;

        if (CurrentUser.isAdmin()) {
            payments = paymentRepository.findAll();
        } else {
            payments = paymentRepository.findByUserId(CurrentUser.getId());
        }

        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }


    public PaymentResponse getPaymentById(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (!CurrentUser.isAdmin()
                && !payment.getUserId().equals(CurrentUser.getId())) {

            throw new AccessDeniedException("Access denied");
        }

        return paymentMapper.toResponse(payment);
    }

}