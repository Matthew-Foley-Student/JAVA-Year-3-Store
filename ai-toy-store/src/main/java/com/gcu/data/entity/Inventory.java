package com.gcu.data.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inventory_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "product_id", nullable = false, unique = true)
	private Product product;

	@Column(name = "quantity_on_hand", nullable = false)
	private int quantityOnHand;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	public Inventory() {
	}

	public Inventory(Product product, int quantityOnHand) {
		this.product = product;
		this.quantityOnHand = quantityOnHand;
	}

	@PrePersist
	@PreUpdate
	public void onSave() {
		updatedAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantityOnHand() {
		return quantityOnHand;
	}

	public void setQuantityOnHand(int quantityOnHand) {
		this.quantityOnHand = quantityOnHand;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
