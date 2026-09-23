package com.aops.payment_service.controller;

import com.aops.payment_service.dto.response.PaymentResponse;
import com.aops.payment_service.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<PaymentResponse> getAllPayments() {

        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public PaymentResponse getPaymentById(
            @PathVariable Long id) {

        return paymentService.getPaymentById(id);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('USER')")
    public PaymentResponse payOrderByPaymentId(
            @PathVariable Long id) {

        return paymentService.payOrderByPaymentId(id);
    }
}