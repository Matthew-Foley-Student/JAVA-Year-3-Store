package com.gcu.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gcu.data.ProductRepository;
import com.gcu.data.ShoppingCartRepository;
import com.gcu.data.UserRepository;
import com.gcu.data.entity.CartItem;
import com.gcu.data.entity.Product;
import com.gcu.data.entity.ShoppingCart;
import com.gcu.data.entity.User;

@Service
public class CartService {

	private final ShoppingCartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public CartService(ShoppingCartRepository cartRepository, ProductRepository productRepository,
			UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	// gets the user's cart, or makes a new one the first time
	@Transactional
	public ShoppingCart getCart(Long userId) {
		ShoppingCart cart = cartRepository.findByUserId(userId).orElse(null);
		if (cart == null) {
			User user = userRepository.findById(userId).orElseThrow();
			cart = cartRepository.save(new ShoppingCart(user));
		}
		return cart;
	}

	@Transactional
	public void addItem(Long userId, Long productId, int quantity) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null || !product.isActive() || quantity < 1) {
			return;
		}
		ShoppingCart cart = getCart(userId);

		// if it's already in the cart just add to the quantity (cart_id + product_id is unique)
		for (CartItem item : cart.getItems()) {
			if (item.getProduct().getId().equals(productId)) {
				item.setQuantity(item.getQuantity() + quantity);
				cartRepository.save(cart);
				return;
			}
		}
		cart.getItems().add(new CartItem(cart, product, quantity));
		cartRepository.save(cart);
	}

	@Transactional
	public void updateQuantity(Long userId, Long cartItemId, int quantity) {
		ShoppingCart cart = getCart(userId);
		if (quantity < 1) {
			cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
		} else {
			for (CartItem item : cart.getItems()) {
				if (item.getId().equals(cartItemId)) {
					item.setQuantity(quantity);
				}
			}
		}
		cartRepository.save(cart);
	}

	@Transactional
	public void removeItem(Long userId, Long cartItemId) {
		ShoppingCart cart = getCart(userId);
		cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
		cartRepository.save(cart);
	}

	@Transactional
	public void clearCart(Long userId) {
		ShoppingCart cart = getCart(userId);
		cart.getItems().clear();
		cartRepository.save(cart);
	}
}
