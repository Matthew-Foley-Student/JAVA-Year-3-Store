package com.gcu.data.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

// table is "orders" because ORDER is a reserved word in SQL
@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	// can be null for guest checkout (then guestEmail is used)
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "guest_email")
	private String guestEmail;

	@Column(name = "order_status", nullable = false, length = 30)
	private String status = "PENDING";

	@Column(precision = 12, scale = 2, nullable = false)
	private BigDecimal subtotal = BigDecimal.ZERO;

	@Column(name = "promo_discount", precision = 12, scale = 2, nullable = false)
	private BigDecimal promoDiscount = BigDecimal.ZERO;

	@Column(name = "shipping_amount", precision = 12, scale = 2, nullable = false)
	private BigDecimal shippingAmount = BigDecimal.ZERO;

	@Column(name = "tax_amount", precision = 12, scale = 2, nullable = false)
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Column(name = "order_total", precision = 12, scale = 2, nullable = false)
	private BigDecimal orderTotal = BigDecimal.ZERO;

	@Column(name = "placed_at", nullable = false)
	private LocalDateTime placedAt;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<OrderItem> items = new ArrayList<>();

	public Order() {
	}

	@PrePersist
	public void onCreate() {
		placedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getGuestEmail() {
		return guestEmail;
	}

	public void setGuestEmail(String guestEmail) {
		this.guestEmail = guestEmail;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getPromoDiscount() {
		return promoDiscount;
	}

	public void setPromoDiscount(BigDecimal promoDiscount) {
		this.promoDiscount = promoDiscount;
	}

	public BigDecimal getShippingAmount() {
		return shippingAmount;
	}

	public void setShippingAmount(BigDecimal shippingAmount) {
		this.shippingAmount = shippingAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(BigDecimal orderTotal) {
		this.orderTotal = orderTotal;
	}

	public LocalDateTime getPlacedAt() {
		return placedAt;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
}
