# CST-339 AI Toy Store — Expanded Database Deliverables

**Course:** CST-339 Programming in Java III  
**Database Developer:** Dominick Spegnolo  
**Database:** `ai_toy_store`  
**Technology:** MySQL Server 8.0.46 and MySQL Workbench  
**Expected tables:** 22

## Overview

This database supports the team's AI Toy Online Retail Store project. It provides relational data storage for product listings, inventory, customer accounts, registered-user shopping carts, orders, shipping, promotions, loyalty rewards, store announcements, polls, and support messages.

The original schema contains eight tables. A second SQL script adds 14 tables and expands selected existing tables. Database creation and the 22-table count were verified in MySQL Workbench; full Spring Boot integration and feature-level testing remain to be completed.

## Files

The following files are located together in this deliverables directory:

| File | Purpose |
| --- | --- |
| `ai_toy_store_schema.sql` | Creates the original eight tables and sample data. |
| `ai_toy_store_phase2_expansion.sql` | Adds 14 tables and expands the initial schema. |
| `ai_toy_store.mwb.bak` | MySQL Workbench model backup; **not** a verified primary `.mwb` file. |
| `ai_toy_store_expanded_erd.png` | Expanded 22-table Entity Relationship Diagram (ERD). |
| `22_table_verification.png` | MySQL Workbench screenshot confirming the 22-table count. |
| `01_Database_Tables.png` | Initial table structure screenshot. |
| `02_Database_ERD.png` | Initial ERD screenshot. |
| `03_Product_Inventory.png` | Sample products and inventory screenshot. |
| `04_SQL_Execution.png` | Initial SQL execution screenshot. |
| `05_Database_Creation.png` | Initial database creation screenshot. |
| `Database_Design_Milestone_1.docx` | Database design write-up for the group project report. |

## Database Tables

**Original eight:** `users`, `categories`, `products`, `inventory`, `shopping_carts`, `cart_items`, `orders`, `order_items`.

**Expanded 14:** `customer_addresses`, `shipping_methods`, `order_addresses`, `promo_codes`, `order_promotions`, `loyalty_accounts`, `loyalty_transactions`, `announcements`, `store_events`, `polls`, `poll_options`, `poll_votes`, `support_messages`, `product_compatibility`.

## Recreate the Database From Scratch

1. Install MySQL Server 8.x and MySQL Workbench, and connect Workbench to your local MySQL server.
2. Open `ai_toy_store_schema.sql` in Workbench and execute it to create the initial database, eight tables, and sample data.
3. Open `ai_toy_store_phase2_expansion.sql` and execute it **once** to expand the schema to 22 tables.
4. Run the following verification query:

```sql
SELECT COUNT(*) AS total_tables
FROM information_schema.tables
WHERE table_schema = 'ai_toy_store'
  AND table_type = 'BASE TABLE';
```

**Expected result:** `22`.

5. Open `ai_toy_store_expanded_erd.png` to review the table relationships.

> **Important:** The Phase 2 script contains schema alterations. Do not rerun it against a database that has already been expanded. The instructions above are for a fresh database setup.

## Current Status

**Completed:** MySQL installation and setup; original eight-table schema; 14-table expansion; initial sample product and inventory queries; verification of 22 tables; expanded ERD; documentation.

**Still to complete:** More extensive constraints and CRUD testing; Java entity and Spring Data JPA repository integration; application-level authentication, checkout, inventory, promotions, and loyalty functionality; end-to-end testing.

## Collaboration and Security

The database is intended for integration with the team's Java Spring Boot application. Coordinate entity mappings and repository methods with the backend developer before changing table definitions.

Do not commit database passwords, connection secrets, payment card data, or actual customer information to the public repository. The `.mwb.bak` file is a model backup and may need to be restored through MySQL Workbench to obtain an editable model.
