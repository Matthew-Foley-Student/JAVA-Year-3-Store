package com.gcu.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.gcu.data.entity.Category;
import com.gcu.data.entity.Inventory;
import com.gcu.data.entity.Product;
import com.gcu.model.ProductForm;

// Milestone 3 version of the product module: no database.
// Products are kept in a map in memory, so new ones are gone when the app restarts.
// It starts with the same categories and products as Dominick's SQL script.
// This class has no @Service on purpose. SpringConfig creates it as a bean (IoC).
public class InMemoryProductService implements ProductServiceInterface {

	private final Map<Long, Product> products = new ConcurrentHashMap<>();
	private final Map<Integer, Category> categories = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	// Spring calls this right after it creates the bean (initMethod in SpringConfig)
	@Override
	public void init() {
		System.out.println("InMemoryProductService.init(): loading sample products (no database)");
		addCategory(1, "AI Toy Models", "Robotic dogs, cats, and dolls");
		addCategory(2, "Accessories", "Chargers, clothing, and devices");
		addCategory(3, "Add-ons", "Additional features and attachments for AI toys");
		addCategory(4, "Merchandise", "Travel bags, mugs, and shirts");

		addSample("TOY-DOG-001", "AI Robot Dog", "Interactive robotic dog", "149.99", 1, true);
		addSample("TOY-CAT-001", "AI Robot Cat", "Interactive robotic cat", "139.99", 1, true);
		addSample("TOY-DOLL-001", "AI Doll", "Interactive AI doll", "129.99", 1, false);
		addSample("ACC-CHARGER-001", "Toy Charger", "Compatible charging accessory", "19.99", 2, false);
		addSample("MERCH-MUG-001", "Brand Mug", "Logo merchandise mug", "14.99", 4, false);
	}

	// Spring calls this when the app shuts down (destroyMethod in SpringConfig)
	@Override
	public void destroy() {
		System.out.println("InMemoryProductService.destroy(): clearing " + products.size() + " products");
		products.clear();
	}

	@Override
	public List<Product> getAllProducts() {
		return products.values().stream()
				.sorted(Comparator.comparing(Product::getId))
				.collect(Collectors.toList());
	}

	@Override
	public List<Product> getActiveProducts(String sort) {
		Comparator<Product> order = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
		if ("price-low".equals(sort)) {
			order = Comparator.comparing(Product::getPrice);
		} else if ("price-high".equals(sort)) {
			order = Comparator.comparing(Product::getPrice).reversed();
		}
		return products.values().stream()
				.filter(Product::isActive)
				.sorted(order)
				.collect(Collectors.toList());
	}

	@Override
	public List<Product> getProductsByCategory(String categoryName) {
		return products.values().stream()
				.filter(p -> p.isActive() && p.getCategory().getName().equals(categoryName))
				.sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
				.collect(Collectors.toList());
	}

	@Override
	public List<Product> getFeaturedProducts() {
		return products.values().stream()
				.filter(p -> p.isActive() && p.isFeatured())
				.collect(Collectors.toList());
	}

	// newest 4 products, so a product you just created shows up first on the home page
	@Override
	public List<Product> getNewArrivals() {
		return products.values().stream()
				.filter(Product::isActive)
				.sorted(Comparator.comparing(Product::getId).reversed())
				.limit(4)
				.collect(Collectors.toList());
	}

	@Override
	public Product getProduct(Long id) {
		return id == null ? null : products.get(id);
	}

	@Override
	public List<Category> getCategories() {
		List<Category> list = new ArrayList<>(categories.values());
		list.sort(Comparator.comparing(Category::getId));
		return list;
	}

	@Override
	public boolean skuExists(String sku) {
		String key = sku.trim();
		return products.values().stream().anyMatch(p -> p.getSku().equalsIgnoreCase(key));
	}

	@Override
	public Product createProduct(ProductForm form) {
		Product product = new Product();
		product.setId(nextId.getAndIncrement());
		copyFormToProduct(form, product);
		product.onCreate(); // sets the created date like the database version does
		product.setInventory(new Inventory(product, form.getQuantity()));
		products.put(product.getId(), product);
		return product;
	}

	@Override
	public Product updateProduct(ProductForm form) {
		Product product = getProduct(form.getId());
		if (product == null) {
			return null;
		}
		copyFormToProduct(form, product);
		product.getInventory().setQuantityOnHand(form.getQuantity());
		return product;
	}

	// no carts or orders point at these products without a database, so we can just remove it
	@Override
	public void deleteProduct(Long id) {
		products.remove(id);
	}

	@Override
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
		product.setCategory(categories.get(form.getCategoryId()));
		product.setSku(form.getSku().trim());
		product.setName(form.getName().trim());
		product.setDescription(form.getDescription());
		product.setPrice(form.getPrice());
		product.setFeatured(form.isFeatured());
	}

	private void addCategory(int id, String name, String description) {
		Category category = new Category();
		category.setId(id);
		category.setName(name);
		category.setDescription(description);
		categories.put(id, category);
	}

	private void addSample(String sku, String name, String description, String price, int categoryId,
			boolean featured) {
		ProductForm form = new ProductForm();
		form.setSku(sku);
		form.setName(name);
		form.setDescription(description);
		form.setPrice(new BigDecimal(price));
		form.setCategoryId(categoryId);
		form.setQuantity(10);
		form.setFeatured(featured);
		createProduct(form);
	}
}
