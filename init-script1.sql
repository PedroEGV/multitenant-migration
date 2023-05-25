CREATE SCHEMA IF NOT EXISTS tenant1;
CREATE SCHEMA IF NOT EXISTS tenant2;

CREATE TABLE IF NOT EXISTS tenant1.customers (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS tenant2.customers (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL
);

-- Seed
INSERT INTO tenant1.customers(name, email) VALUES ('User 1', 'user1@host.com');
INSERT INTO tenant2.customers(name, email) VALUES ('User 2', 'user2@host.com');
