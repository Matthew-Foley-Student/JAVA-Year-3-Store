package com.gcu.business;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gcu.data.CartItemRepository;
import com.gcu.data.CategoryRepository;
import com.gcu.data.InventoryRepository;
import com.gcu.data.OrderItemRepository;
import com.gcu.data.ProductRepository;
import com.gcu.data.entity.Category;
import com.gcu.data.entity.Inventory;
import com.gcu.data.entity.Product;
import com.gcu.model.ProductForm;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final InventoryRepository inventoryRepository;
	private final CartItemRepository cartItemRepository;
	private final OrderItemRepository orderItemRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
			InventoryRepository inventoryRepository, CartItemRepository cartItemRepository,
			OrderItemRepository orderItemRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.inventoryRepository = inventoryRepository;
		this.cartItemRepository = cartItemRepository;
		this.orderItemRepository = orderItemRepository;
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	// sort can be "name", "price-low" or "price-high" (from the shop page dropdown)
	public List<Product> getActiveProducts(String sort) {
		if ("price-low".equals(sort)) {
			return productRepository.findByActiveTrueOrderByPriceAsc();
		} else if ("price-high".equals(sort)) {
			return productRepository.findByActiveTrueOrderByPriceDesc();
		}
		return productRepository.findByActiveTrueOrderByNameAsc();
	}

	public List<Product> getProductsByCategory(String categoryName) {
		return productRepository.findByCategoryNameAndActiveTrue(categoryName);
	}

	public List<Product> getFeaturedProducts() {
		return productRepository.findByFeaturedTrueAndActiveTrue();
	}

	public List<Product> getNewArrivals() {
		return productRepository.findTop4ByActiveTrueOrderByCreatedAtDesc();
	}

	public Product getProduct(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	public List<Category> getCategories() {
		return categoryRepository.findAll();
	}

	public boolean skuExists(String sku) {
		return productRepository.existsBySku(sku);
	}

	@Transactional
	public Product createProduct(ProductForm form) {
		Product product = new Product();
		copyFormToProduct(form, product);
		product = productRepository.save(product);

		// every product gets a row in inventory
		Inventory inventory = new Inventory(product, form.getQuantity());
		inventoryRepository.save(inventory);
		product.setInventory(inventory);
		return product;
	}

	@Transactional
	public Product updateProduct(ProductForm form) {
		Product product = productRepository.findById(form.getId()).orElse(null);
		if (product == null) {
			return null;
		}
		copyFormToProduct(form, product);
		productRepository.save(product);

		Inventory inventory = inventoryRepository.findByProductId(product.getId())
				.orElse(new Inventory(product, 0));
		inventory.setQuantityOnHand(form.getQuantity());
		inventoryRepository.save(inventory);
		return product;
	}

	// if the product is already in a cart or an order the foreign keys won't let us delete it,
	// so in that case we just set it inactive so it stops showing in the shop
	@Transactional
	public void deleteProduct(Long id) {
		Product product = productRepository.findById(id).orElse(null);
		if (product == null) {
			return;
		}
		if (cartItemRepository.existsByProductId(id) || orderItemRepository.existsByProductId(id)) {
			product.setActive(false);
			productRepository.save(product);
		} else {
			inventoryRepository.findByProductId(id).ifPresent(inventoryRepository::delete);
			productRepository.delete(product);
		}
	}

	// used to fill in the update form with the current values
	public ProductForm toForm(Product product) {
		ProductForm form = new ProductForm();
		form.setId(product.getId());
		form.setSku(product.getSku());
		form.setName(product.getName());
		form.setDescription(product.getDescription());
		form.setPrice(product.getPrice());
		form.setCategoryId(product.getCategory().getId());
		form.setQuantity(product.getStock());
		form.setFeatured(product.isFeatured());
		return form;
	}

	private void copyFormToProduct(ProductForm form, Product product) {
		Category category = categoryRepository.findById(form.getCategoryId()).orElse(null);
		product.setCategory(category);
		product.setSku(form.getSku());
		product.setName(form.getName());
		product.setDescription(form.getDescription());
		product.setPrice(form.getPrice());
		product.setFeatured(form.isFeatured());
	}
}
