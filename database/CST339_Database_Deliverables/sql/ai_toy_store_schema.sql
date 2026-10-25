-- CST-339 CLC: AI Toy Store relational schema
-- MySQL 8.0; safe to re-run schema creation without deleting existing data.
-- Execute in MySQL Workbench against your local server.
CREATE DATABASE IF NOT EXISTS ai_toy_store CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE ai_toy_store;

CREATE TABLE IF NOT EXISTS categories (
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
  product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id INT NOT NULL,
  sku VARCHAR(64) NOT NULL UNIQUE,
  product_name VARCHAR(150) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
  CONSTRAINT chk_products_price CHECK (price >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory (
  inventory_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL UNIQUE,
  quantity_on_hand INT NOT NULL DEFAULT 0,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(product_id),
  CONSTRAINT chk_inventory_quantity CHECK (quantity_on_hand >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shopping_carts (
  cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cart_items (
  cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cart_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES shopping_carts(cart_id) ON DELETE CASCADE,
  CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(product_id),
  CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id),
  CONSTRAINT chk_cart_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
  order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  order_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  order_total DECIMAL(12,2) NOT NULL DEFAULT 0,
  placed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT chk_order_total CHECK (order_total >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
  order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(product_id),
  CONSTRAINT chk_order_item_quantity CHECK (quantity > 0),
  CONSTRAINT chk_order_item_price CHECK (unit_price >= 0)
) ENGINE=InnoDB;

-- Seed categories only when absent. Existing records remain unchanged.
INSERT INTO categories (category_name, description) VALUES
('AI Toy Models', 'Robotic dogs, cats, and dolls'),
('Accessories', 'Chargers, clothing, and devices'),
('Add-ons', 'Additional features and attachments for AI toys'),
('Merchandise', 'Travel bags, mugs, and shirts')
ON DUPLICATE KEY UPDATE category_name = category_name;

-- Example products, inserted only once per SKU.
INSERT INTO products (category_id, sku, product_name, description, price)
SELECT c.category_id, seed.sku, seed.product_name, seed.description, seed.price
FROM (
  SELECT 'TOY-DOG-001' AS sku, 'AI Robot Dog' AS product_name, 'Interactive robotic dog' AS description, 149.99 AS price, 'AI Toy Models' AS category_name
  UNION ALL SELECT 'TOY-CAT-001', 'AI Robot Cat', 'Interactive robotic cat', 139.99, 'AI Toy Models'
  UNION ALL SELECT 'TOY-DOLL-001', 'AI Doll', 'Interactive AI doll', 129.99, 'AI Toy Models'
  UNION ALL SELECT 'ACC-CHARGER-001', 'Toy Charger', 'Compatible charging accessory', 19.99, 'Accessories'
  UNION ALL SELECT 'MERCH-MUG-001', 'Brand Mug', 'Logo merchandise mug', 14.99, 'Merchandise'
) AS seed
JOIN categories AS c ON c.category_name = seed.category_name
WHERE NOT EXISTS (SELECT 1 FROM products p WHERE p.sku = seed.sku);

INSERT INTO inventory (product_id, quantity_on_hand)
SELECT p.product_id, 10 FROM products p
WHERE p.sku IN ('TOY-DOG-001','TOY-CAT-001','TOY-DOLL-001','ACC-CHARGER-001','MERCH-MUG-001')
AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.product_id = p.product_id);

-- Validation queries
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'ai_toy_store' AND table_type = 'BASE TABLE' ORDER BY table_name;
SELECT p.product_name, c.category_name, p.price, i.quantity_on_hand
FROM products p JOIN categories c ON p.category_id = c.category_id
LEFT JOIN inventory i ON i.product_id = p.product_id ORDER BY p.product_id;
