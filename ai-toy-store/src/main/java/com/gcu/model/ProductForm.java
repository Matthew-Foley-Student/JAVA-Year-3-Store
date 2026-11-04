package com.gcu.model;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

// form object for the create and update product pages
public class ProductForm {

	private Long id;

	@NotBlank(message = "SKU is required")
	@Size(max = 64)
	private String sku;

	@NotBlank(message = "Product name is required")
	@Size(max = 150)
	private String name;

	private String description;

	@NotNull(message = "Price is required")
	@DecimalMin(value = "0.00", message = "Price can't be negative")
	private BigDecimal price;

	@NotNull(message = "Pick a category")
	private Integer categoryId;

	@Min(value = 0, message = "Quantity can't be negative")
	private int quantity;

	private boolean featured;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}
}
