package com.gcu.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gcu.business.ProductService;
import com.gcu.data.entity.Product;
import com.gcu.model.ProductForm;

// admin pages to create, view, update and delete products
// TODO: lock these down to ADMIN users once we add Spring Security
@Controller
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("title", "Manage Products");
		model.addAttribute("products", productService.getAllProducts());
		return "products";
	}

	@GetMapping("/{id}")
	public String details(@PathVariable Long id, Model model) {
		Product product = productService.getProduct(id);
		if (product == null) {
			return "redirect:/products";
		}
		model.addAttribute("title", product.getName());
		model.addAttribute("product", product);
		return "product-details";
	}

	@GetMapping("/create")
	public String createForm(Model model) {
		model.addAttribute("title", "Create Product");
		model.addAttribute("productForm", new ProductForm());
		model.addAttribute("categories", productService.getCategories());
		return "product-create";
	}

	@PostMapping("/create")
	public String create(@Valid @ModelAttribute ProductForm productForm, BindingResult result, Model model) {
		if (productForm.getSku() != null && productService.skuExists(productForm.getSku())) {
			result.rejectValue("sku", "duplicate", "That SKU is already used");
		}
		if (result.hasErrors()) {
			model.addAttribute("title", "Create Product");
			model.addAttribute("categories", productService.getCategories());
			return "product-create";
		}
		Product product = productService.createProduct(productForm);
		return "redirect:/products/" + product.getId();
	}

	@GetMapping("/update/{id}")
	public String updateForm(@PathVariable Long id, Model model) {
		Product product = productService.getProduct(id);
		if (product == null) {
			return "redirect:/products";
		}
		model.addAttribute("title", "Update Product");
		model.addAttribute("productForm", productService.toForm(product));
		model.addAttribute("categories", productService.getCategories());
		return "product-update";
	}

	@PostMapping("/update")
	public String update(@Valid @ModelAttribute ProductForm productForm, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("title", "Update Product");
			model.addAttribute("categories", productService.getCategories());
			return "product-update";
		}
		productService.updateProduct(productForm);
		return "redirect:/products/" + productForm.getId();
	}

	@GetMapping("/delete/{id}")
	public String deleteConfirm(@PathVariable Long id, Model model) {
		Product product = productService.getProduct(id);
		if (product == null) {
			return "redirect:/products";
		}
		model.addAttribute("title", "Delete Product");
		model.addAttribute("product", product);
		return "product-delete";
	}

	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "redirect:/products";
	}
}
