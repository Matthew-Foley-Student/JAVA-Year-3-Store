# CST-339 CLC Milestone 3 — Database Design Update

**Project:** AI Toy Store  
**Database platform:** MySQL 8.0.46 / MySQL Workbench  
**Database:** `ai_toy_store`  
**Database developer:** Dominick Spegnolo

## Tasks completed / design review

The existing database design was reviewed for the Milestone 3 Product Creation module. The original schema defines eight tables; the Phase 2 expansion defines fourteen additional tables, for an expected total of 22. The two supplied scripts define product, category, inventory, shopping cart, order, and supporting store relationships. Actual execution and the current database state should be verified in MySQL Workbench.

A read-only validation script accompanies this report. It checks table count, product-related columns, foreign keys, sample products, and product/inventory CHECK constraints. No database integration is required by Milestone 3.

## General technical approach

The project uses a relational MySQL schema for future persistence, while Milestone 3 uses Spring MVC controllers, business-service Spring Beans, dependency injection, and Thymeleaf layouts without requiring live database writes. Product input can be validated and processed in memory. In Milestone 4, a persistence layer can connect Java domain objects to the existing schema.

## Product data model

| Table | Relevant fields | Purpose |
|---|---|---|
| `categories` | `category_id`, `category_name`, `description` | Product classification |
| `products` | `product_id`, `category_id`, `sku`, `product_name`, `description`, `price`, `active`, `created_at`, `release_date`, `is_featured` | Product catalog |
| `inventory` | `inventory_id`, `product_id`, `quantity_on_hand`, `updated_at` | Stock tracking |

`products.category_id` references `categories.category_id` (many products per category). `inventory.product_id` is unique and references `products.product_id` (at most one inventory row per product). The schema does not itself guarantee that every product has an inventory row; that can be handled by service logic during persistence.

## Key technical decisions

- **Separate inventory table:** Avoids duplicating quantity in the product record and provides a dedicated stock model.
- **`DECIMAL(10,2)` price:** Avoids floating-point rounding issues for monetary values.
- **Unique SKU:** Prevents duplicate product identifiers.
- **Foreign keys:** Preserve valid category and product references.
- **CHECK constraints:** Prevent negative product prices and negative inventory quantities in MySQL 8.0.46.
- **No persistence in Milestone 3:** Meets the assignment requirement while keeping the schema ready for future integration.

## Suggested Spring MVC validation alignment

The Java Product model should validate product name, SKU, category selection, and nonnegative price. If quantity is collected on the Product Creation form, validate it as a nonnegative integer and map it to the separate inventory entity when persistence is added. Coordinate exact Java property names and types with the Product Creation developer. Use `BigDecimal` for monetary values in Java.

## Install and configuration instructions

1. Install MySQL Server 8.x and MySQL Workbench.
2. Execute `ai_toy_store_schema.sql` on a fresh local instance.
3. Execute `ai_toy_store_phase2_expansion.sql` **once**; its `ALTER TABLE` statements are not idempotent.
4. Execute `ai_toy_store_milestone3_validation.sql` to inspect the resulting structure.
5. Confirm 22 base tables, correct product columns and relationships, and existing sample products.
6. Keep database credentials out of the public Git repository.

## Known issues and risks

- **Java mapping not yet confirmed:** The Product Creation form/model may use different property names, such as `name` instead of `productName`, or collect fields not present in the schema.
- **No runtime persistence:** Product submissions are not required to be stored until Milestone 4.
- **Migration repeatability:** The Phase 2 migration must not be rerun on an already-expanded database.
- **Stock-row creation:** The database permits products with no corresponding inventory row; future service logic should address this if stock tracking is mandatory.
- **Application-level validation:** Input constraints must also be enforced in the Spring MVC layer; database constraints alone do not satisfy Milestone 3 form validation.

## ERD summary

`categories` (1) → (many) `products` (1) → (0..1) `inventory`.

The expanded ERD from the earlier milestone should be included in the team's updated Design Report. If the database schema is unchanged, the same ERD remains applicable; label it as reviewed for Milestone 3 rather than claiming new schema changes.

## Documentation / submission checklist

- [ ] Include current expanded 22-table ERD in the Design Report.
- [ ] Reference both existing DDL scripts and the new read-only validation script.
- [ ] Confirm Java Product model and form fields with the Product Creation developer.
- [ ] Capture Workbench screenshots of verification query results.
- [ ] Include updated technical decisions, setup instructions, and risks in the group report.
- [ ] Participate in peer code review and record review outcomes.
- [ ] Commit files to the correct branch and coordinate merging with the team.
- [ ] Confirm team screencast demonstrates the working application.

**Status:** Documentation and validation script prepared from the supplied DDL. Execution, peer review, and Java model alignment remain to be confirmed.
