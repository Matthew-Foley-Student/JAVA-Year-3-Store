package com.gcu.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	boolean existsByProductId(Long productId);
}
