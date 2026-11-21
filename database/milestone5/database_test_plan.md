# Milestone 5 Database Test Plan

| ID | Test | Expected Result | Observed Result | Status |
|---|---|---|---|---|
| DB-01 | Start Spring Boot | Application starts | Started successfully | PASS |
| DB-02 | Retrieve products | Product records displayed | Five products displayed | PASS |
| DB-03 | Update product price | New price persisted | Toy Charger showed 24.99 | PASS |
| DB-04 | Create test product | New product persisted | Pre-deletion SQL evidence not captured | PARTIAL |
| DB-05 | Delete test product | Test product absent | Query returned zero records | PASS |
| DB-06 | Verify schema | Expected tables exist | 22 tables listed | PASS |
| DB-07 | Inspect foreign keys | Relationships defined | Foreign-key rows returned | PASS |
| DB-08 | Verify inventory | Products join to inventory | Five products, quantity 10 each | PASS |

## Follow-up
- Confirm Toy Charger price was restored to 19.99.
- Resolve Spring Data JPA versus JDBC rubric requirement.
- Preserve screenshots in evidence/.
- Do not commit database passwords.
