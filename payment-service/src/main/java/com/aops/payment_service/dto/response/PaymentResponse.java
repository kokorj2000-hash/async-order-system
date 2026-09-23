package com.aops.payment_service.dto.response;

import com.aops.common.model.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {

    private Long id;
    private Long userId;
    private Long orderId;
    private BigDecimal amount;
    private OrderStatus status;

}
