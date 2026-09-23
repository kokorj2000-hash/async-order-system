package com.aops.order_service.controller;

import com.aops.common.model.OrderStatus;
import com.aops.order_service.dto.response.OrderResponse;
import com.aops.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.context.annotation.Import;
import com.aops.order_service.config.SecurityConfig;

@Import(SecurityConfig.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldCreateOrder() throws Exception {

        // Arrange
        OrderResponse response = new OrderResponse();
        response.setId(1L);
        response.setUserId(10L);
        response.setAmount(BigDecimal.valueOf(1200));
        response.setStatus(OrderStatus.CREATED);

        when(orderService.createOrder(any()))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "amount":1200
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.amount").value(1200))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void shouldReturn401WhenUserIsNotAuthenticated() throws Exception {

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "amount":1200
                            }
                            """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturn403WhenAdminCreatesOrder() throws Exception {

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "amount":1200
                            }
                            """))
                .andExpect(status().isForbidden());
    }
}