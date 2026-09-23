package com.aops.order_service.repository;

import com.aops.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

}
