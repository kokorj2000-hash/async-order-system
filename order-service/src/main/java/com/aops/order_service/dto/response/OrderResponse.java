package com.aops.order_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.aops.common.model.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponse {

    @Schema(description = "Order identifier")
    private Long id;

    @Schema(description = "User identifier")
    private Long userId;

    @Schema(description = "Order amount")
    private BigDecimal amount;

    @Schema(description = "Current order status")
    private OrderStatus status;

}