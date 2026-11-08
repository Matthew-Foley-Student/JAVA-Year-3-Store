package com.gcu.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gcu.data.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	Optional<Category> findByName(String name);
}
