
SET search_path TO dev;

-- Insérer cinq utilisateurs
INSERT INTO users (username, email, password)
VALUES ('Jean', 'jean@gmail.com', '123'),
       ('Pierre', 'pierre@gmail.com', '123'),
       ('Paul', 'paul@gmail.com', '123'),
       ('Jacques', 'jacques@gmail.com', '123'),
       ('Denis', 'denis@gmail.com', '123');

-- Insérer des connexions entre utilisateurs
INSERT INTO relationships (requester_id, receiver_id, created_at, validated_at)
VALUES (1, 2, '2023-01-01 10:00:00', '2023-01-02 10:00:00'),
       (2, 3, '2023-02-01 11:00:00', '2023-02-02 11:00:00'),
       (3, 4, '2023-03-01 12:00:00', '2023-03-02 12:00:00'),
       (4, 5, '2023-04-01 13:00:00', '2023-04-02 13:00:00'),
       (5, 1, '2023-05-01 14:00:00', '2023-05-02 14:00:00');

-- Insérer des comptes pour chaque utilisateur
INSERT INTO accounts (user_id, balance)
VALUES (1, 1100.00),
       (2, 2200.00),
       (3, 1300.00),
       (4, 3400.00),
       (5, 2500.00);

-- Insérer des transactions entre utilisateurs
INSERT INTO transactions (sender_id, receiver_id, amount, description)
VALUES (1, 2, 50.00, ''),
       (2, 3, 75.00, 'Cadeau non mérité'),
       (3, 4, 100.00, 'Remboursement pour la blague de mauvais goût'),
       (4, 5, 25.00, 'Cheval en plastique'),
       (5, 1, 10.00, 'Don pour sauver les pandas en Antarctique');