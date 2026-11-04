package com.gcu.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	Optional<Inventory> findByProductId(Long productId);
}
