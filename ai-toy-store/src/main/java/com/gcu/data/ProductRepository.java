package com.gcu.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByActiveTrueOrderByNameAsc();

	List<Product> findByActiveTrueOrderByPriceAsc();

	List<Product> findByActiveTrueOrderByPriceDesc();

	List<Product> findByCategoryNameAndActiveTrue(String categoryName);

	List<Product> findByFeaturedTrueAndActiveTrue();

	List<Product> findTop4ByActiveTrueOrderByCreatedAtDesc();

	boolean existsBySku(String sku);
}
