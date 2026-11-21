# Milestone 5 — Database Verification

## Scope
MySQL database verification for the AI Toy Store project.

## Environment
- MySQL 8.0
- Spring Boot 2.7.18
- Java 17
- Database: ai_toy_store
- Product persistence: Spring Data JPA

## Verified Results
| Test | Result |
|---|---|
| Spring Boot startup | PASS |
| MySQL-backed ProductService activated | PASS |
| Product list retrieved | PASS |
| Product price update persisted | PASS |
| Test product absent after deletion | PASS |
| 22 database tables present | PASS |
| Foreign-key relationships listed | PASS |
| Five products joined to inventory | PASS |

## Important Notes
- Product CREATE was exercised, but a separate pre-deletion SQL result was not captured.
- Foreign-key metadata was inspected; constraint enforcement was not independently tested.
- ProductService uses Spring Data JPA. The assignment specifies Spring JDBC or Spring Data JDBC; the team must resolve this difference.
- Local database credentials must never be committed.

## Screenshots
See the evidence folder for supporting screenshots.
