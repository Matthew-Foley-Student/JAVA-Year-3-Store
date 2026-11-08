package com.gcu.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

	Optional<ShoppingCart> findByUserId(Long userId);
}
