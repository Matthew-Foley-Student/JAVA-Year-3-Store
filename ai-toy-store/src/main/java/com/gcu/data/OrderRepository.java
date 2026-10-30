package com.gcu.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUserIdOrderByPlacedAtDesc(Long userId);
}
