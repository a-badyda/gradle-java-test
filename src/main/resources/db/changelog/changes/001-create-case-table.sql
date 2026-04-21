--liquibase formatted sql
--changeset test:1
CREATE TABLE cases
(
  id                VARCHAR(26) PRIMARY KEY,
  case_number       VARCHAR(50) UNIQUE NOT NULL,
  title             VARCHAR(250)       NOT NULL,
  description       TEXT,
  status            VARCHAR(20)        NOT NULL,
  created_date      TIMESTAMPTZ        NOT NULL,
  last_updated_date TIMESTAMPTZ        NOT NULL
);

