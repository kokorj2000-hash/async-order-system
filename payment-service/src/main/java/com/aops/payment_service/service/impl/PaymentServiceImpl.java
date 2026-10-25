package com.aops.payment_service.service.impl;

import com.aops.common.constant.KafkaTopics;
import com.aops.common.event.OrderCreatedEvent;
import com.aops.common.event.OrderPaidEvent;
import com.aops.common.model.OrderStatus;
import com.aops.common.security.CurrentUser;
import com.aops.payment_service.dto.response.PaymentResponse;
import com.aops.payment_service.exception.PaymentNotFoundException;
import com.aops.payment_service.mapper.PaymentMapper;
import com.aops.payment_service.model.Payment;
import com.aops.payment_service.repository.PaymentRepository;
import com.aops.payment_service.service.OutboxService;
import com.aops.payment_service.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OutboxService outboxService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              OutboxService outboxService) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.outboxService = outboxService;
    }

    @Override
    public void createPayment(OrderCreatedEvent event) {

        if (paymentRepository.existsByOrderId(event.getId())) {
            log.info("Payment for orderId={} already exists, skipping duplicate event", event.getId());
            return;
        }

        Payment payment = new Payment();
        payment.setOrderId(event.getId());
        payment.setUserId(event.getUserId());
        payment.setAmount(event.getAmount());
        payment.setStatus(event.getStatus());

        try {
            paymentRepository.save(payment);
        } catch (DataIntegrityViolationException e) {
            log.info("Concurrent duplicate payment ignored for orderId={}", event.getId());
        }
    }

    @Override
    @Transactional
    public PaymentResponse payOrderByPaymentId(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (!payment.getUserId().equals(CurrentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        if (payment.getStatus() == OrderStatus.PAID) {
            log.info("Payment id={} is already PAID, skipping", id);
            return paymentMapper.toResponse(payment);
        }

        payment.setStatus(OrderStatus.PAID);
        paymentRepository.save(payment);

        OrderPaidEvent event = new OrderPaidEvent();
        event.setOrderId(payment.getOrderId());
        event.setUserId(payment.getUserId());

        outboxService.save(
                KafkaTopics.PAYMENT_COMPLETED,
                payment.getOrderId().toString(),
                event
        );

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