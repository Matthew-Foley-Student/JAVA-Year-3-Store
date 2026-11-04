package com.gcu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gcu.business.ProductService;

@Controller
public class HomeController {

	private final ProductService productService;

	public HomeController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping({ "/", "/home" })
	public String home(Model model) {
		model.addAttribute("title", "Home");
		model.addAttribute("featured", productService.getFeaturedProducts());
		model.addAttribute("newArrivals", productService.getNewArrivals());
		return "home";
	}

	@GetMapping("/about-us")
	public String aboutUs(Model model) {
		model.addAttribute("title", "About Us");
		return "about-us";
	}

	@GetMapping("/help-faqs")
	public String helpFaqs(Model model) {
		model.addAttribute("title", "Help & FAQs");
		return "help-faqs";
	}

	@GetMapping("/contact-us")
	public String contactUs(Model model) {
		model.addAttribute("title", "Contact Us");
		return "contact-us";
	}
}
