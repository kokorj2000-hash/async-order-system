package com.aops.order_service.controller;

import com.aops.order_service.dto.request.CreateOrderRequest;
import com.aops.order_service.dto.response.OrderResponse;
import com.aops.order_service.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Get orders",
            description = "Returns orders available to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<OrderResponse> getAllOrders() {

        return orderService.getAllOrders();
    }

    @Operation(
            summary = "Get order by ID",
            description = "Returns an order by its identifier"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public OrderResponse getOrderById(
            @PathVariable Long id) {

        return orderService.getOrderById(id);
    }

    @Operation(
            summary = "Create order",
            description = "Creates a new order for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public OrderResponse createOrder(
            @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(request);
    }

    @Operation(
            summary = "Delete order",
            description = "Deletes an order belonging to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ResponseStatus(code = org.springframework.http.HttpStatus.NO_CONTENT)
    public void deleteOrderById(
            @PathVariable Long id) {

        orderService.deleteOrderById(id);
    }
}