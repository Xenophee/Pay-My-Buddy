-- Insérer cinq utilisateurs
INSERT INTO users (username, email, password)
VALUES ('user1', 'user1@example.com', 'password1'),
       ('user2', 'user2@example.com', 'password2'),
       ('user3', 'user3@example.com', 'password3'),
       ('user4', 'user4@example.com', 'password4'),
       ('user5', 'user5@example.com', 'password5');

-- Insérer des connexions entre utilisateurs
INSERT INTO connections (requester_id, receiver_id, validated_at, is_read)
VALUES (1, 2, CURRENT_TIMESTAMP, TRUE),
       (2, 3, CURRENT_TIMESTAMP, TRUE),
       (3, 4, CURRENT_TIMESTAMP, TRUE),
       (4, 5, CURRENT_TIMESTAMP, TRUE),
       (5, 1, CURRENT_TIMESTAMP, TRUE);

-- Insérer des comptes pour chaque utilisateur
INSERT INTO accounts (user_id, balance)
VALUES (1, 1100.00),
       (2, 2200.00),
       (3, 1300.00),
       (4, 3400.00),
       (5, 2500.00);

-- Insérer des transactions entre utilisateurs
INSERT INTO transactions (sender_id, receiver_id, amount, description)
VALUES (1, 2, 50.00, 'Payment for services'),
       (2, 3, 75.00, 'Cadeau'),
       (3, 4, 100.00, 'Loan repayment'),
       (4, 5, 25.00, 'Dinner payment'),
       (5, 1, 10.00, 'Coffee payment');