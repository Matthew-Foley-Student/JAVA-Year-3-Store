-- CST-339 AI Toy Store: Phase 2 expansion
-- Run AFTER ai_toy_store_schema.sql on MySQL 8.0.46.
-- Existing 8 tables and records are preserved. Run this migration ONCE.
USE ai_toy_store;

-- Extend customer profiles and product merchandising.
ALTER TABLE users
  ADD COLUMN phone VARCHAR(25) NULL,
  ADD COLUMN date_of_birth DATE NULL,
  ADD COLUMN updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE products
  ADD COLUMN release_date DATE NULL,
  ADD COLUMN is_featured BOOLEAN NOT NULL DEFAULT FALSE;

-- Permit orders without a registered account (guest checkout).
ALTER TABLE orders
  MODIFY COLUMN user_id BIGINT NULL,
  ADD COLUMN guest_email VARCHAR(255) NULL,
  ADD COLUMN promo_discount DECIMAL(12,2) NOT NULL DEFAULT 0,
  ADD COLUMN shipping_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  ADD COLUMN tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  ADD COLUMN subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
  ADD CONSTRAINT chk_orders_nonnegative_amounts CHECK
    (promo_discount >= 0 AND shipping_amount >= 0 AND tax_amount >= 0 AND subtotal >= 0),
  ADD CONSTRAINT chk_orders_customer CHECK (user_id IS NOT NULL OR guest_email IS NOT NULL);

CREATE TABLE IF NOT EXISTS customer_addresses (
  address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  address_label VARCHAR(50),
  recipient_name VARCHAR(150) NOT NULL,
  address_line1 VARCHAR(200) NOT NULL,
  address_line2 VARCHAR(200),
  city VARCHAR(100) NOT NULL,
  state_region VARCHAR(100) NOT NULL,
  postal_code VARCHAR(20) NOT NULL,
  country_code CHAR(2) NOT NULL DEFAULT 'US',
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS shipping_methods (
  shipping_method_id INT AUTO_INCREMENT PRIMARY KEY,
  method_name VARCHAR(100) NOT NULL UNIQUE,
  base_price DECIMAL(10,2) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_shipping_price CHECK (base_price >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_addresses (
  order_address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL,
  address_type VARCHAR(20) NOT NULL,
  recipient_name VARCHAR(150) NOT NULL,
  address_line1 VARCHAR(200) NOT NULL,
  address_line2 VARCHAR(200),
  city VARCHAR(100) NOT NULL,
  state_region VARCHAR(100) NOT NULL,
  postal_code VARCHAR(20) NOT NULL,
  country_code CHAR(2) NOT NULL DEFAULT 'US',
  CONSTRAINT fk_order_address_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT uq_order_address_type UNIQUE (order_id, address_type),
  CONSTRAINT chk_order_address_type CHECK (address_type IN ('SHIPPING','BILLING'))
) ENGINE=InnoDB;

ALTER TABLE orders
  ADD COLUMN shipping_method_id INT NULL,
  ADD CONSTRAINT fk_orders_shipping_method FOREIGN KEY (shipping_method_id)
    REFERENCES shipping_methods(shipping_method_id);

CREATE TABLE IF NOT EXISTS promo_codes (
  promo_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(50) NOT NULL UNIQUE,
  discount_type VARCHAR(10) NOT NULL,
  discount_value DECIMAL(10,2) NOT NULL,
  starts_at DATETIME NULL,
  expires_at DATETIME NULL,
  max_uses INT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_promo_type CHECK (discount_type IN ('PERCENT','FIXED')),
  CONSTRAINT chk_promo_value CHECK (discount_value > 0),
  CONSTRAINT chk_promo_percent CHECK (discount_type <> 'PERCENT' OR discount_value <= 100),
  CONSTRAINT chk_promo_uses CHECK (max_uses IS NULL OR max_uses >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_promotions (
  order_id BIGINT NOT NULL,
  promo_id BIGINT NOT NULL,
  applied_discount DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (order_id, promo_id),
  CONSTRAINT fk_order_promo_order FOREIGN KEY (order_id) REFERENCES orders(order_id),
  CONSTRAINT fk_order_promo_promo FOREIGN KEY (promo_id) REFERENCES promo_codes(promo_id),
  CONSTRAINT chk_applied_discount CHECK (applied_discount >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS loyalty_accounts (
  loyalty_account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  points_balance INT NOT NULL DEFAULT 0,
  tier VARCHAR(30) NOT NULL DEFAULT 'STANDARD',
  CONSTRAINT fk_loyalty_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT chk_loyalty_balance CHECK (points_balance >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS loyalty_transactions (
  loyalty_transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  loyalty_account_id BIGINT NOT NULL,
  order_id BIGINT NULL,
  points_change INT NOT NULL,
  reason VARCHAR(100) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_loyalty_tx_account FOREIGN KEY (loyalty_account_id) REFERENCES loyalty_accounts(loyalty_account_id),
  CONSTRAINT fk_loyalty_tx_order FOREIGN KEY (order_id) REFERENCES orders(order_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS announcements (
  announcement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  body TEXT NOT NULL,
  published_at DATETIME NULL,
  active BOOLEAN NOT NULL DEFAULT FALSE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS store_events (
  event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_name VARCHAR(150) NOT NULL,
  description TEXT,
  starts_at DATETIME NOT NULL,
  ends_at DATETIME NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_event_dates CHECK (ends_at IS NULL OR ends_at >= starts_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS polls (
  poll_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  question VARCHAR(255) NOT NULL,
  opens_at DATETIME NULL,
  closes_at DATETIME NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT chk_poll_dates CHECK (closes_at IS NULL OR opens_at IS NULL OR closes_at >= opens_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS poll_options (
  option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  poll_id BIGINT NOT NULL,
  option_text VARCHAR(200) NOT NULL,
  CONSTRAINT fk_poll_option_poll FOREIGN KEY (poll_id) REFERENCES polls(poll_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS poll_votes (
  vote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  poll_id BIGINT NOT NULL,
  option_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  voted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_vote_poll FOREIGN KEY (poll_id) REFERENCES polls(poll_id),
  CONSTRAINT fk_vote_option FOREIGN KEY (option_id) REFERENCES poll_options(option_id),
  CONSTRAINT fk_vote_user FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT uq_poll_user UNIQUE (poll_id, user_id)
) ENGINE=InnoDB;
-- Application must validate that poll_votes.option_id belongs to poll_votes.poll_id.

CREATE TABLE IF NOT EXISTS support_messages (
  message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  contact_email VARCHAR(255) NOT NULL,
  subject VARCHAR(200) NOT NULL,
  message_body TEXT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'NEW',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_support_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS product_compatibility (
  parent_product_id BIGINT NOT NULL,
  accessory_product_id BIGINT NOT NULL,
  PRIMARY KEY (parent_product_id, accessory_product_id),
  CONSTRAINT fk_compat_parent FOREIGN KEY (parent_product_id) REFERENCES products(product_id),
  CONSTRAINT fk_compat_accessory FOREIGN KEY (accessory_product_id) REFERENCES products(product_id),
  CONSTRAINT chk_compat_distinct CHECK (parent_product_id <> accessory_product_id)
) ENGINE=InnoDB;

-- Safe sample additions (do not duplicate records).
INSERT INTO shipping_methods (method_name, base_price) VALUES
 ('Standard Shipping', 5.99), ('Express Shipping', 14.99)
ON DUPLICATE KEY UPDATE method_name = method_name;

-- Review created tables; expected total is 22.
SELECT COUNT(*) AS total_tables FROM information_schema.tables
WHERE table_schema = 'ai_toy_store' AND table_type = 'BASE TABLE';
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'ai_toy_store' AND table_type = 'BASE TABLE' ORDER BY table_name;
