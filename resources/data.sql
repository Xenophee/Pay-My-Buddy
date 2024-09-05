
SET search_path TO dev;

-- Insérer cinq utilisateurs
INSERT INTO users (username, email, password)
VALUES ('Jean', 'jean@gmail.com', '$2a$10$QWDIWUBgiITPXVTEJZ8goeZGmgVuEM6pyhsia1zZ2BLeyznBbNWly'),
       ('Pierre', 'pierre@gmail.com', '$2a$10$QWDIWUBgiITPXVTEJZ8goeZGmgVuEM6pyhsia1zZ2BLeyznBbNWly'),
       ('Paul', 'paul@gmail.com', '$2a$10$QWDIWUBgiITPXVTEJZ8goeZGmgVuEM6pyhsia1zZ2BLeyznBbNWly'),
       ('Jacques', 'jacques@gmail.com', '$2a$10$QWDIWUBgiITPXVTEJZ8goeZGmgVuEM6pyhsia1zZ2BLeyznBbNWly'),
       ('Denis', 'denis@gmail.com', '$2a$10$QWDIWUBgiITPXVTEJZ8goeZGmgVuEM6pyhsia1zZ2BLeyznBbNWly');

-- Le mdp est Abc123@!

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
INSERT INTO transactions (sender_id, receiver_id, amount, description, processed_at)
VALUES (1, 2, 50.00, '', '2023-06-01 15:00:00'),
       (2, 3, 75.00, 'Cadeau non mérité', '2023-07-01 16:00:00'),
       (3, 4, 100.00, 'Remboursement pour la blague de mauvais goût', '2023-08-01 17:00:00'),
       (4, 5, 25.00, 'Cheval en plastique', '2023-09-01 18:00:00'),
       (5, 1, 10.00, 'Don pour sauver les pandas en Antarctique', '2023-10-01 19:00:00');