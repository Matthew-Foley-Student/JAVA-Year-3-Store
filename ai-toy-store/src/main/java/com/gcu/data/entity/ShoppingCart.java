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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "shopping_carts")
public class ShoppingCart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long id;

	// each registered user has one cart
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<CartItem> items = new ArrayList<>();

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public ShoppingCart() {
	}

	public ShoppingCart(User user) {
		this.user = user;
	}

	@PrePersist
	@PreUpdate
	public void onSave() {
		updatedAt = LocalDateTime.now();
	}

	public BigDecimal getSubtotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (CartItem item : items) {
			total = total.add(item.getLineTotal());
		}
		return total;
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

	public List<CartItem> getItems() {
		return items;
	}

	public void setItems(List<CartItem> items) {
		this.items = items;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
