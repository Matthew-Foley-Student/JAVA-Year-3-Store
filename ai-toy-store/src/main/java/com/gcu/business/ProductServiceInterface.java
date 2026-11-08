package com.gcu.business;

import java.util.List;

import com.gcu.data.entity.Category;
import com.gcu.data.entity.Product;
import com.gcu.model.ProductForm;

// the controllers only talk to this interface, so we can swap how products are stored
// Milestone 3: InMemoryProductService (no database)
// Milestone 4: ProductService (MySQL) by setting app.products.in-memory=false
// SpringConfig decides which one gets created as the "productService" bean
public interface ProductServiceInterface {

	List<Product> getAllProducts();

	// sort can be "name", "price-low" or "price-high" (from the shop page dropdown)
	List<Product> getActiveProducts(String sort);

	List<Product> getProductsByCategory(String categoryName);

	List<Product> getFeaturedProducts();

	List<Product> getNewArrivals();

	Product getProduct(Long id);

	List<Category> getCategories();

	boolean skuExists(String sku);

	Product createProduct(ProductForm form);

	Product updateProduct(ProductForm form);

	void deleteProduct(Long id);

	// used to fill in the update form with the current values
	ProductForm toForm(Product product);

	// Spring calls these when it creates and destroys the bean (see SpringConfig)
	void init();

	void destroy();
}
