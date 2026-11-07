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
		model.addAttribute("primaryTitle", "Shop");
		model.addAttribute("heading", "All Products");
		model.addAttribute("sort", sort);
		model.addAttribute("products", productService.getActiveProducts(sort));
		model.addAttribute("activeCategory", "all");
		return "shop";
	}

	// category pages from the sitemap. category names match the categories table
	@GetMapping("/shop/ai-toy-models")
	public String aiToyModels(@RequestParam(defaultValue = "name") String sort, Model model) {
	    model.addAttribute("primaryTitle", "Shop");
	    model.addAttribute("activeCategory", "toys");
	    model.addAttribute("sort", sort);
	    return showCategory("AI Toy Models", sort, model);
	}

	@GetMapping("/shop/accessories")
	public String accessories(@RequestParam(defaultValue = "name") String sort, Model model) {
		model.addAttribute("primaryTitle", "Shop");
		model.addAttribute("activeCategory", "accessories");
		model.addAttribute("sort", sort);
		return showCategory("Accessories", sort, model);
	}
		
	// We can remove this category. Did not need this one - Brittney
	@GetMapping("/shop/add-ons")
	public String addOns(@RequestParam(defaultValue = "name") String sort, Model model) {
		return showCategory("Add-ons", sort,  model);
	}

	@GetMapping("/shop/merch")
	public String merch(@RequestParam(defaultValue = "name") String sort, Model model) {
		model.addAttribute("primaryTitle", "Shop");
		model.addAttribute("activeCategory", "merch");
		model.addAttribute("sort", sort);
		return showCategory("Merchandise", sort, model);
	}

	@GetMapping("/shop/item/{id}")
	public String viewItem(@PathVariable Long id, Model model) {
		Product product = productService.getProduct(id);
		if (product == null || !product.isActive()) {
			return "redirect:/shop";
		}
		model.addAttribute("primaryTitle", "Shop");
		model.addAttribute("title", product.getName());
		model.addAttribute("product", product);
		return "view-item";
	}
	


	// all the category pages use the shop template for now, 
	// No need to split the categories into separate pages, since the mapping is done. The default page 
	// is doing what it needs to do since the method returns the default shop page- Brittney 
	private String showCategory(String categoryName, String sort, Model model) {

	    model.addAttribute("primaryTitle", "Shop");
	    model.addAttribute("title", categoryName);
	    model.addAttribute("heading", categoryName);
	    model.addAttribute("sort", sort);

	    model.addAttribute("products",
	            productService.getProductsByCategory(categoryName));
		return "shop";
	}
}
