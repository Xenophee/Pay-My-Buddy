
-- ? Ce fichier contient les triggers pour l'environnement de développement et de test.

-- ! Exécuter ce script SQL dans pgAdmin en dernier, après celui des tables et celui des données.

-- =======================================================================================================
-- =======================================================================================================
-- * TRIGGERS POUR L'ENVIRONNEMENT DE DEVELOPPEMENT
-- =======================================================================================================
-- =======================================================================================================


SET search_path TO dev;

---------------------------------------------------
-- Création de la fonction pour mettre à jour la date de modification
---------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

---------------------------------------------------
-- Création du trigger pour mettre à jour la date de modification
---------------------------------------------------
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


-- =======================================================================================================


---------------------------------------------------
-- Création de la fonction pour créer un compte après l'insertion d'un utilisateur
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
-- Création du trigger pour créer un compte après l'insertion d'un utilisateur
---------------------------------------------------
CREATE TRIGGER after_user_insert
    AFTER INSERT ON users
    FOR EACH ROW
EXECUTE FUNCTION create_account_after_user_insert();


-- =======================================================================================================


---------------------------------------------------
-- Création de la fonction pour mettre à jour les comptes après l'insertion d'une transaction
---------------------------------------------------
CREATE OR REPLACE FUNCTION handle_transaction()
    RETURNS TRIGGER AS $$
BEGIN
    -- Vérifier si le solde du donneur est suffisant
    IF (SELECT balance FROM accounts WHERE user_id = NEW.sender_id) < NEW.amount THEN
        RAISE EXCEPTION 'Solde insuffisant pour effectuer la transaction';
    ELSE

        -- Débiter le compte donneur
        UPDATE accounts
        SET balance = balance - NEW.amount
        WHERE user_id = NEW.sender_id;

        -- Créditer le compte receveur
        UPDATE accounts
        SET balance = balance + NEW.amount
        WHERE user_id = NEW.receiver_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


---------------------------------------------------
-- Création du trigger pour mettre à jour les comptes après l'insertion d'une transaction
---------------------------------------------------
CREATE TRIGGER before_transaction_insert
    BEFORE INSERT ON transactions
    FOR EACH ROW
EXECUTE FUNCTION handle_transaction();



-- =======================================================================================================
-- =======================================================================================================
-- * TRIGGERS POUR L'ENVIRONNEMENT DE TEST
-- =======================================================================================================
-- =======================================================================================================


SET search_path TO test;

---------------------------------------------------
-- Création de la fonction pour mettre à jour la date de modification
---------------------------------------------------
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

---------------------------------------------------
-- Création du trigger pour mettre à jour la date de modification
---------------------------------------------------
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


-- =======================================================================================================


---------------------------------------------------
-- Création de la fonction pour créer un compte après l'insertion d'un utilisateur
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
-- Création du trigger pour créer un compte après l'insertion d'un utilisateur
---------------------------------------------------
CREATE TRIGGER after_user_insert
    AFTER INSERT ON users
    FOR EACH ROW
EXECUTE FUNCTION create_account_after_user_insert();


-- =======================================================================================================


---------------------------------------------------
-- Création de la fonction pour mettre à jour les comptes après l'insertion d'une transaction
---------------------------------------------------
CREATE OR REPLACE FUNCTION handle_transaction()
    RETURNS TRIGGER AS $$
BEGIN
    -- Vérifier si le solde du donneur est suffisant
    IF (SELECT balance FROM accounts WHERE user_id = NEW.sender_id) < NEW.amount THEN
        RAISE EXCEPTION 'Solde insuffisant pour effectuer la transaction';
    ELSE

        -- Débiter le compte donneur
        UPDATE accounts
        SET balance = balance - NEW.amount
        WHERE user_id = NEW.sender_id;

        -- Créditer le compte receveur
        UPDATE accounts
        SET balance = balance + NEW.amount
        WHERE user_id = NEW.receiver_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


---------------------------------------------------
-- Création du trigger pour mettre à jour les comptes après l'insertion d'une transaction
---------------------------------------------------
CREATE TRIGGER before_transaction_insert
    BEFORE INSERT ON transactions
    FOR EACH ROW
EXECUTE FUNCTION handle_transaction();