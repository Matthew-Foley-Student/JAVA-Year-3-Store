-- CST-339 CLC Milestone 4
-- AI Toy Store Database Validation
-- Read-only verification script

USE ai_toy_store;

-- 1. Database table count
SELECT COUNT(*) AS total_tables
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'ai_toy_store'
  AND TABLE_TYPE = 'BASE TABLE';

-- 2. InnoDB table count
SELECT COUNT(*) AS innodb_tables
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'ai_toy_store'
  AND ENGINE = 'InnoDB';

-- 3. Foreign key relationship count
SELECT COUNT(*) AS foreign_keys
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'ai_toy_store'
  AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 4. Auto-increment primary key count
SELECT COUNT(*) AS auto_increment_primary_keys
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'ai_toy_store'
  AND COLUMN_KEY = 'PRI'
  AND EXTRA LIKE '%auto_increment%';

-- 5. Invalid product-category relationships
SELECT COUNT(*) AS invalid_categories
FROM products p
LEFT JOIN categories c
    ON p.category_id = c.category_id
WHERE c.category_id IS NULL;

-- 6. Negative product prices
SELECT COUNT(*) AS negative_prices
FROM products
WHERE price < 0;

-- 7. Login and registration column verification
SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'ai_toy_store'
  AND TABLE_NAME = 'users'
ORDER BY ORDINAL_POSITION;

-- 8. Product creation column verification
SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'ai_toy_store'
  AND TABLE_NAME = 'products'
ORDER BY ORDINAL_POSITION;
