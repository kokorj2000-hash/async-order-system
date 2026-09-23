package com.aops.order_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;


public class CreateOrderRequest {

    @Schema(
            description = "Order amount",
            example = "1200.50"
    )
    private BigDecimal amount;

    public CreateOrderRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
