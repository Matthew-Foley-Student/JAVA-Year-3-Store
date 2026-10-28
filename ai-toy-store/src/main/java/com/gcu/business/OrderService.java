package com.gcu.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gcu.data.InventoryRepository;
import com.gcu.data.OrderRepository;
import com.gcu.data.entity.CartItem;
import com.gcu.data.entity.Inventory;
import com.gcu.data.entity.Order;
import com.gcu.data.entity.OrderItem;
import com.gcu.data.entity.ShoppingCart;

@Service
public class OrderService {

	// TODO: get these from the shipping_methods table and add promo codes later
	private static final BigDecimal TAX_RATE = new BigDecimal("0.08");
	private static final BigDecimal SHIPPING = new BigDecimal("5.99");

	private final OrderRepository orderRepository;
	private final InventoryRepository inventoryRepository;
	private final CartService cartService;

	public OrderService(OrderRepository orderRepository, InventoryRepository inventoryRepository,
			CartService cartService) {
		this.orderRepository = orderRepository;
		this.inventoryRepository = inventoryRepository;
		this.cartService = cartService;
	}

	public BigDecimal calculateTax(BigDecimal subtotal) {
		return subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
	}

	public BigDecimal getShipping() {
		return SHIPPING;
	}

	// turns the cart into an order. everything is in one transaction so if one item
	// is out of stock nothing gets saved
	@Transactional
	public Order placeOrder(Long userId) {
		ShoppingCart cart = cartService.getCart(userId);
		if (cart.getItems().isEmpty()) {
			throw new IllegalStateException("Your cart is empty");
		}

		Order order = new Order();
		order.setUser(cart.getUser());

		for (CartItem item : cart.getItems()) {
			Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId()).orElse(null);
			if (inventory == null || inventory.getQuantityOnHand() < item.getQuantity()) {
				throw new IllegalStateException("Not enough stock for " + item.getProduct().getName());
			}
			inventory.setQuantityOnHand(inventory.getQuantityOnHand() - item.getQuantity());
			inventoryRepository.save(inventory);

			order.getItems().add(new OrderItem(order, item.getProduct(), item.getQuantity(),
					item.getProduct().getPrice()));
		}

		BigDecimal subtotal = cart.getSubtotal();
		BigDecimal tax = calculateTax(subtotal);
		order.setSubtotal(subtotal);
		order.setTaxAmount(tax);
		order.setShippingAmount(SHIPPING);
		order.setOrderTotal(subtotal.add(tax).add(SHIPPING));

		order = orderRepository.save(order);
		cartService.clearCart(userId);
		return order;
	}

	public List<Order> getOrdersForUser(Long userId) {
		return orderRepository.findByUserIdOrderByPlacedAtDesc(userId);
	}
}
