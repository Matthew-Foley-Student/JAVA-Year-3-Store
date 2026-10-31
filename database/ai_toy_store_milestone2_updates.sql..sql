USE ai_toy_store;

-- CST-339 CLC Milestone 2
-- Optional username field for future account features.
-- Current login uses email.
-- Run once after the original schema and Phase 2 expansion.

ALTER TABLE users
ADD COLUMN username VARCHAR(100) NULL;