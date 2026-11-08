-- CST-339 CLC Milestone 3: database design verification
-- Read-only checks; does not create, alter, or delete data.
-- Run after the base schema and Phase 2 expansion have been applied.
USE ai_toy_store;

-- Verify expected total table count (22).
SELECT COUNT(*) AS total_tables
FROM information_schema.tables
WHERE table_schema = 'ai_toy_store' AND table_type = 'BASE TABLE';

-- Verify product-related column definitions.
SELECT table_name, column_name, column_type, is_nullable, column_key
FROM information_schema.columns
WHERE table_schema = 'ai_toy_store'
  AND table_name IN ('products', 'categories', 'inventory')
ORDER BY FIELD(table_name, 'categories', 'products', 'inventory'), ordinal_position;

-- Verify product relationships and referential constraints.
SELECT table_name, column_name, referenced_table_name, referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'ai_toy_store'
  AND referenced_table_name IS NOT NULL
  AND table_name IN ('products', 'inventory', 'cart_items', 'order_items', 'product_compatibility')
ORDER BY table_name, column_name;

-- Verify sample product records and inventory, without modifying data.
SELECT p.product_id, p.sku, p.product_name, c.category_name,
       p.description, p.price, p.active, p.release_date, p.is_featured,
       i.quantity_on_hand
FROM products p
JOIN categories c ON c.category_id = p.category_id
LEFT JOIN inventory i ON i.product_id = p.product_id
ORDER BY p.product_id;

-- Inspect relevant CHECK constraints (MySQL 8.0.16+ enforces CHECK).
SELECT tc.table_name, tc.constraint_name, cc.check_clause
FROM information_schema.table_constraints tc
JOIN information_schema.check_constraints cc
  ON cc.constraint_schema = tc.constraint_schema
 AND cc.constraint_name = tc.constraint_name
WHERE tc.table_schema = 'ai_toy_store'
  AND tc.table_name IN ('products', 'inventory')
  AND tc.constraint_type = 'CHECK'
ORDER BY tc.table_name, tc.constraint_name;
