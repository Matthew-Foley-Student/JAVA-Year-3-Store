package com.gcu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.gcu.business.ProductService;
import com.gcu.data.entity.Product;

@Controller
public class ShopController {

	private final ProductService productService;

	public ShopController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/shop")
	public String shop(@RequestParam(defaultValue = "name") String sort, Model model) {
		model.addAttribute("title", "Shop");
		model.addAttribute("heading", "All Products");
		model.addAttribute("sort", sort);
		model.addAttribute("products", productService.getActiveProducts(sort));
		return "shop";
	}

	// category pages from the sitemap. category names match the categories table
	@GetMapping("/shop/ai-toy-models")
	public String aiToyModels(Model model) {
		return showCategory("AI Toy Models", model);
	}

	@GetMapping("/shop/accessories")
	public String accessories(Model model) {
		return showCategory("Accessories", model);
	}

	@GetMapping("/shop/add-ons")
	public String addOns(Model model) {
		return showCategory("Add-ons", model);
	}

	@GetMapping("/shop/merch")
	public String merch(Model model) {
		return showCategory("Merchandise", model);
	}

	@GetMapping("/shop/item/{id}")
	public String viewItem(@PathVariable Long id, Model model) {
		Product product = productService.getProduct(id);
		if (product == null || !product.isActive()) {
			return "redirect:/shop";
		}
		model.addAttribute("title", product.getName());
		model.addAttribute("product", product);
		return "view-item";
	}

	// all the category pages use the shop template for now, Brittney can split them up later
	private String showCategory(String categoryName, Model model) {
		model.addAttribute("title", categoryName);
		model.addAttribute("heading", categoryName);
		model.addAttribute("products", productService.getProductsByCategory(categoryName));
		return "shop";
	}
}
