# CST-339 AI Toy Store — Expanded Database Deliverables

Database developer: Dominick Spegnolo
MySQL Server: 8.0.46
Schema: ai_toy_store
Expected tables after both SQL scripts: 22

## Files
sql/ai_toy_store_schema.sql — creates the original 8 tables and sample data
sql/ai_toy_store_phase2_expansion.sql — adds 14 tables and expands existing tables
models/ai_toy_store.mwb.bak — MySQL Workbench backup model supplied by the developer (NOT the primary .mwb model file)
screenshots/ai_toy_store_expanded_erd.png — exported 22-table ERD
screenshots/22_table_verification.png — Workbench verification of 22 tables
screenshots/01–05 — initial implementation screenshots
documentation/Database_Design_Milestone_1.docx — section for Matthew's project report

## Recreate from scratch
1. Start MySQL Server 8.x and open MySQL Workbench.
2. Run sql/ai_toy_store_schema.sql first.
3. Run sql/ai_toy_store_phase2_expansion.sql ONCE.
4. Run SELECT COUNT(*) FROM information_schema.tables
   WHERE table_schema='ai_toy_store' AND table_type='BASE TABLE';
   Expected: 22.
5. Reverse engineer the schema in Workbench to inspect the ERD.

IMPORTANT: Do not rerun the phase 2 ALTER TABLE migration on an already expanded database.
The .mwb.bak file is a Workbench model backup archive, not a verified primary .mwb save.
If you need the editable model, open/restore this backup in Workbench or supply the original .mwb file.
Do not upload secrets, database passwords, or actual customer data.

Status: Schema/table creation and sample queries verified by screenshots; Spring Boot integration and complete feature testing are not yet verified.
