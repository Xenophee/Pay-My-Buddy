
DROP DATABASE IF EXISTS pay_my_buddy;

CREATE DATABASE pay_my_buddy
    WITH ENCODING='UTF8'
    LC_COLLATE='fr_FR.UTF-8'
    LC_CTYPE='fr_FR.UTF-8'
    TEMPLATE=template0;


CREATE SCHEMA IF NOT EXISTS dev;
SET search_path TO dev;

---------------------------------------------------
-- Créer la table des utilisateurs
---------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(250) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

---------------------------------------------------
-- Créer la table des connexions entre utilisateurs
---------------------------------------------------
CREATE TABLE IF NOT EXISTS relationships (
    requester_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    receiver_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    validated_at TIMESTAMP,
    PRIMARY KEY (requester_id, receiver_id)
);

---------------------------------------------------
-- Créer la table des comptes
---------------------------------------------------
CREATE TABLE IF NOT EXISTS accounts (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    balance NUMERIC(15, 2) DEFAULT 0.00
);

---------------------------------------------------
-- Créer la table des transactions
---------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    receiver_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    description VARCHAR(250),
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
