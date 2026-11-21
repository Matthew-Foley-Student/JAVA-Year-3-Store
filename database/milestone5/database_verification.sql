USE ai_toy_store;

-- Verify all database tables
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'ai_toy_store'
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Verify foreign-key definitions
SELECT table_name, column_name,
       referenced_table_name, referenced_column_name
FROM information_schema.key_column_usage
WHERE table_schema = 'ai_toy_store'
  AND referenced_table_name IS NOT NULL
ORDER BY table_name, column_name;

-- Verify product inventory
SELECT p.product_id, p.product_name, i.quantity_on_hand
FROM products p
LEFT JOIN inventory i ON p.product_id = i.product_id
ORDER BY p.product_id;

-- Verify original product update
SELECT product_id, sku, product_name, price, active
FROM products
WHERE product_id = 1;

-- Verify test product cleanup
SELECT product_id, sku, product_name, active
FROM products
WHERE sku = 'M5-DB-TEST-001';
