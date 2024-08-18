
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
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(250) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

---------------------------------------------------
-- Créer la table des connexions entre utilisateurs
---------------------------------------------------
CREATE TABLE IF NOT EXISTS connections (
    requester_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    receiver_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    validated_at TIMESTAMP,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (requester_id, receiver_id),
    CHECK (requester_id <> receiver_id)
);

---------------------------------------------------
-- Créer la table des comptes
---------------------------------------------------
CREATE TABLE IF NOT EXISTS accounts (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00
);

---------------------------------------------------
-- Créer la table des transactions
---------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    receiver_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    amount NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    description VARCHAR(500),
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


---------------------------------------------------
-- Créer la fonction pour créer un compte après l'insertion d'un utilisateur
---------------------------------------------------
CREATE OR REPLACE FUNCTION create_account_after_user_insert()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO accounts (user_id)
    VALUES (NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

---------------------------------------------------
-- Créer le trigger pour créer un compte après l'insertion d'un utilisateur
---------------------------------------------------
CREATE TRIGGER after_user_insert
    AFTER INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION create_account_after_user_insert();