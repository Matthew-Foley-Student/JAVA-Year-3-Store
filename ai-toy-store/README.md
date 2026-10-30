# AI Toy Store - Spring Boot App (Backend)

**Backend Developer:** Dimitri Jimenez

Spring Boot 2.7 / Java 17 / Spring Data JPA / Thymeleaf / MySQL

## How to run

1. Set up the database first using Dominick's scripts in the repo root
   (`ai_toy_store_schema.sql` then `ai_toy_store_phase2_expansion.sql`).
2. In STS: File > Import > Maven > Existing Maven Projects and pick the `ai-toy-store` folder.
3. Open `src/main/resources/application.properties` and set your MySQL username and password.
   Don't commit your real password. You can also set a `DB_PASSWORD` environment variable
   in the run configuration instead.
4. Run `AiToyStoreApplication` as a Spring Boot App and go to http://localhost:8080

Hibernate is set to `ddl-auto=none`, so it uses the tables from the SQL scripts and never changes them.

## Layers (N-Layer)

| Package | What's in it |
| --- | --- |
| `com.gcu.controller` | Controllers with the @GetMapping/@PostMapping routes for each page |
| `com.gcu.business` | Services with the business logic (products, users, cart, orders) |
| `com.gcu.data` | Spring Data JPA repositories |
| `com.gcu.data.entity` | JPA entities that map to the database tables |
| `com.gcu.model` | Form objects with validation for the create/update/sign up/login pages |

## Pages

| URL | Page |
| --- | --- |
| `/` or `/home` | Home (new arrivals, featured products) |
| `/shop` | All products, sort by name or price |
| `/shop/ai-toy-models`, `/shop/accessories`, `/shop/add-ons`, `/shop/merch` | Category pages |
| `/shop/item/{id}` | View item, add to cart |
| `/cart` | Cart with subtotal, tax, shipping and place order |
| `/sign-up`, `/login`, `/logout` | Accounts (passwords hashed with BCrypt) |
| `/account` | Account details and purchase history |
| `/about-us`, `/help-faqs`, `/contact-us` | Info pages |
| `/products` | Manage products (list) |
| `/products/create` | Create a product |
| `/products/{id}` | Product details |
| `/products/update/{id}` | Update a product |
| `/products/delete/{id}` | Delete a product |

The templates in `src/main/resources/templates` are basic Bootstrap placeholders so the app runs.
Brittney's HTML can replace them, just keep the `th:` attributes so the data still shows up.
Page layouts use the Thymeleaf Layout Dialect. The main layout is `templates/layouts/defaultTemplate.html`
(head, Bootstrap, nav, footer). Each page starts with `layout:decorate="~{layouts/defaultTemplate}"` and puts its
content in `<main layout:fragment="content">`. The nav is in `templates/fragments/header.html` and the footer is in
`templates/fragments/footer.html`.

## Milestone 2: login and registration without a database

- `UserServiceInterface` is what `AccountController` uses. There are two versions of it:
  - `InMemoryUserService` (on now): users are kept in a map in memory, so they disappear when the app restarts.
  - `UserService`: saves users to the MySQL `users` table.
- Switch between them in `application.properties` with `app.users.in-memory=true` or `false`.
- Test account for the in-memory version: `test@aitoystore.com` / `password123`.
- The cart needs a real user row, so while users are in memory the cart sends you to your account page
  with a message instead. It works again with `app.users.in-memory=false`.

## Notes / still to do

- Entities are done for the 8 core tables. The other 14 (addresses, promo codes, loyalty, polls, etc.) are next.
- Login and sign up don't use the database yet (Milestone 2 requirement). See above.
- Login just saves the user in the session for now. We can switch to Spring Security when we get to it in class, and lock `/products` to admins.
- Cart only works when logged in because `shopping_carts.user_id` is required. Guest cart can come later.
- Tax (8%) and shipping ($5.99) are hard coded in `OrderService` for now.
- If a product is already in a cart or an order, delete hides it from the shop (active = false) instead of deleting it, because of the foreign keys.
