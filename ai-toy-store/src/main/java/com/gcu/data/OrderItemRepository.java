package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	boolean existsByProductId(Long productId);
}
