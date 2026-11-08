package com.gcu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gcu.business.InMemoryProductService;
import com.gcu.business.InMemoryUserService;
import com.gcu.business.ProductService;
import com.gcu.business.ProductServiceInterface;
import com.gcu.business.UserService;
import com.gcu.business.UserServiceInterface;
import com.gcu.data.CartItemRepository;
import com.gcu.data.CategoryRepository;
import com.gcu.data.InventoryRepository;
import com.gcu.data.OrderItemRepository;
import com.gcu.data.ProductRepository;
import com.gcu.data.UserRepository;

// Milestone 3: Spring Beans and IoC (Inversion of Control)
// Instead of the controllers doing "new InMemoryUserService()", Spring creates the services here
// as beans and injects them into the controllers through their constructors (Dependency Injection).
// The controllers only know the interfaces, so switching to the database in Milestone 4 is just a
// property change in application.properties, with no controller code changes.
@Configuration
public class SpringConfig {

	// true = no database (Milestone 3), false = MySQL (Milestone 4)
	@Value("${app.users.in-memory:true}")
	private boolean usersInMemory;

	@Value("${app.products.in-memory:true}")
	private boolean productsInMemory;

	// registration and login service
	@Bean(name = "userService", initMethod = "init", destroyMethod = "destroy")
	public UserServiceInterface getUserService(UserRepository userRepository) {
		if (usersInMemory) {
			return new InMemoryUserService();
		}
		return new UserService(userRepository);
	}

	// product creation module (create, view, update, delete products)
	@Bean(name = "productService", initMethod = "init", destroyMethod = "destroy")
	public ProductServiceInterface getProductService(ProductRepository productRepository,
			CategoryRepository categoryRepository, InventoryRepository inventoryRepository,
			CartItemRepository cartItemRepository, OrderItemRepository orderItemRepository) {
		if (productsInMemory) {
			return new InMemoryProductService();
		}
		return new ProductService(productRepository, categoryRepository, inventoryRepository,
				cartItemRepository, orderItemRepository);
	}
}
