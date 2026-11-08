package com.gcu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.gcu.business.InMemoryProductService;
import com.gcu.business.InMemoryUserService;
import com.gcu.business.ProductServiceInterface;
import com.gcu.business.UserServiceInterface;

// checks the Milestone 3 requirements: Spring creates the services as beans (SpringConfig),
// and product creation, registration and login all work without a database
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class Milestone3Tests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserServiceInterface userService;

	@Autowired
	private ProductServiceInterface productService;

	@Test
	void springInjectsTheInMemoryBeans() {
		assertTrue(userService instanceof InMemoryUserService);
		assertTrue(productService instanceof InMemoryProductService);
		// init() ran, so the sample data is there
		assertTrue(productService.skuExists("TOY-DOG-001"));
		assertNotNull(userService.login("testuser", "password123"));
	}

	@Test
	void createProductWithoutDatabase() throws Exception {
		int before = productService.getAllProducts().size();
		mvc.perform(post("/products/create")
				.param("sku", "TEST-001").param("name", "Test Toy").param("categoryId", "1")
				.param("price", "9.99").param("quantity", "3"))
				.andExpect(status().is3xxRedirection());
		assertEquals(before + 1, productService.getAllProducts().size());
		assertTrue(productService.skuExists("TEST-001"));
	}

	@Test
	void createProductShowsErrors() throws Exception {
		mvc.perform(post("/products/create").param("sku", "TOY-DOG-001"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasFieldErrors("productForm", "sku", "name", "price", "categoryId"));
	}

	@Test
	void signUpThenLogin() throws Exception {
		mvc.perform(post("/sign-up")
				.param("firstName", "Test").param("lastName", "Two").param("email", "two@test.com")
				.param("phone", "602-555-0000").param("username", "testtwo").param("password", "secret123"))
				.andExpect(redirectedUrl("/account"));
		mvc.perform(post("/login").param("username", "testtwo").param("password", "secret123"))
				.andExpect(redirectedUrl("/account"));
		mvc.perform(post("/login").param("username", "testtwo").param("password", "wrong"))
				.andExpect(status().isOk());
	}
}
