# CST-339 CLC Milestone 4
## AI Toy Store — Database Documentation

### Database Overview
Database Name: ai_toy_store
Database Management System: MySQL 8.0.46
Storage Engine: InnoDB
Character Set: utf8mb4

The AI Toy Store database supports the enterprise
N-Layer Spring Boot application.

### Database Architecture
- Total Tables: 22
- InnoDB Tables: 22
- Foreign Key Relationships: 23
- Auto-Increment Primary Keys: 20

### Milestone 4 Modules

#### Login
Table: users
- user_id: Primary key
- email: Unique login identifier
- password_hash: Stores hashed passwords
- role: Defaults to CUSTOMER

Authentication and password verification must
be implemented in the application service layer.

#### Registration
Table: users

Registration requires:
- email
- password_hash
- first_name
- last_name

The database enforces unique email addresses.
Passwords must be securely hashed before insertion.

#### Product Creation
Tables: products, categories

The products table includes:
- product_id
- category_id
- sku
- product_name
- description
- price
- active
- created_at
- release_date
- is_featured

Products reference categories through category_id.
The database enforces nonnegative prices and
unique product SKUs.

### Database Validation
The following checks were completed:

- 22 tables verified
- 22 InnoDB tables verified
- 23 foreign key relationships verified
- 20 auto-increment primary keys identified
- Zero invalid product-category references
- Zero negative product prices
- Temporary test records rolled back successfully

Validation SQL:
../milestone4_validation.sql

### Schema Files
All 22 exported table SQL files are located in:

../schema/

The exports contain CREATE TABLE statements and
may include seed data.

WARNING:
The exports contain DROP TABLE IF EXISTS statements.
Do not execute them against an existing database
containing information that must be preserved.

### Spring JDBC Integration
Recommended architecture:

Spring MVC Controller
        |
Business Service
        |
DAO / Repository
        |
Spring JdbcTemplate
        |
MySQL ai_toy_store

Example JDBC connection configuration:

spring.datasource.url=jdbc:mysql://localhost:3306/ai_toy_store
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

Credentials must be configured securely and
must not be committed to GitHub.

### Known Issues and Considerations
- Username is nullable and not uniquely indexed.
- Email-based login is recommended.
- MySQL 8 collation compatibility must be considered.
- JDBC application integration requires team testing.
- Full application functionality is not verified by
  database-only tests.

### Milestone 4 Database Responsibilities
- Database schema verification
- Foreign key and constraint validation
- Database transaction testing
- SQL schema exports
- Validation script preparation
- ER diagram maintenance
- Database documentation

### Status
Database schema validated and prepared for
Spring JDBC integration.
