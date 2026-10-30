package com.gcu.controller;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gcu.business.CartService;
import com.gcu.business.OrderService;
import com.gcu.data.entity.Order;
import com.gcu.data.entity.ShoppingCart;

// cart only works for logged in users right now since shopping_carts needs a user_id
// guest cart could be added later with the session
@Controller
@RequestMapping("/cart")
public class CartController {

	private final CartService cartService;
	private final OrderService orderService;

	public CartController(CartService cartService, OrderService orderService) {
		this.cartService = cartService;
		this.orderService = orderService;
	}

	@GetMapping
	public String viewCart(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		ShoppingCart cart = cartService.getCart(userId);
		BigDecimal subtotal = cart.getSubtotal();
		BigDecimal tax = orderService.calculateTax(subtotal);
		BigDecimal shipping = cart.getItems().isEmpty() ? BigDecimal.ZERO : orderService.getShipping();

		model.addAttribute("title", "Cart");
		model.addAttribute("cart", cart);
		model.addAttribute("subtotal", subtotal);
		model.addAttribute("tax", tax);
		model.addAttribute("shipping", shipping);
		model.addAttribute("total", subtotal.add(tax).add(shipping));
		return "cart";
	}

	@PostMapping("/add")
	public String add(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity,
			HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		cartService.addItem(userId, productId, quantity);
		return "redirect:/cart";
	}

	@PostMapping("/update")
	public String update(@RequestParam Long cartItemId, @RequestParam int quantity, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		cartService.updateQuantity(userId, cartItemId, quantity);
		return "redirect:/cart";
	}

	@PostMapping("/remove")
	public String remove(@RequestParam Long cartItemId, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		cartService.removeItem(userId, cartItemId);
		return "redirect:/cart";
	}

	@PostMapping("/checkout")
	public String checkout(HttpSession session, RedirectAttributes redirect) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			return "redirect:/login";
		}
		try {
			Order order = orderService.placeOrder(userId);
			redirect.addFlashAttribute("message", "Order #" + order.getId() + " placed!");
			return "redirect:/account";
		} catch (IllegalStateException e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/cart";
		}
	}

	// Milestone 2: users are only in memory, so they don't have a row in the users table yet
	// and the cart can't be created. Show a message instead of an error page.
	@ExceptionHandler(NoSuchElementException.class)
	public String cartUnavailable(RedirectAttributes redirect) {
		redirect.addFlashAttribute("message", "The cart will be available once accounts are saved to the database (Milestone 3).");
		return "redirect:/account";
	}
}
