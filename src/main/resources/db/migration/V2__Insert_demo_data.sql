INSERT INTO users (email, username, password, first_name, last_name, role, enabled) VALUES
('admin@taskflow.com', 'admin', '$2a$10$eImiTXuWVxfM37uY4JANjOzkXYqYPvtQdXQyHNQXQQGQCYXfYqYHK', 'Admin', 'User', 'ADMIN', TRUE),
('john.doe@taskflow.com', 'johndoe', '$2a$10$eImiTXuWVxfM37uY4JANjOzkXYqYPvtQdXQyHNQXQQGQCYXfYqYHK', 'John', 'Doe', 'USER', TRUE),
('jane.smith@taskflow.com', 'janesmith', '$2a$10$eImiTXuWVxfM37uY4JANjOzkXYqYPvtQdXQyHNQXQQGQCYXfYqYHK', 'Jane', 'Smith', 'USER', TRUE);

INSERT INTO tasks (title, description, status, priority, creator_id, due_date) VALUES
('Configurer le projet', 'Mise en place de l''environnement de développement', 'DONE', 'HIGH', 1, CURRENT_TIMESTAMP + INTERVAL '7 days'),
('Développer l''API REST', 'Créer les endpoints pour la gestion des tâches', 'IN_PROGRESS', 'URGENT', 1, CURRENT_TIMESTAMP + INTERVAL '14 days'),
('Écrire la documentation', 'Documenter l''API avec Swagger', 'TODO', 'MEDIUM', 2, CURRENT_TIMESTAMP + INTERVAL '21 days'),
('Tests unitaires', 'Atteindre 70% de couverture de code', 'TODO', 'HIGH', 2, CURRENT_TIMESTAMP + INTERVAL '10 days'),
('Déploiement', 'Déployer l''application sur Render', 'TODO', 'LOW', 3, CURRENT_TIMESTAMP + INTERVAL '30 days');

INSERT INTO task_assignments (task_id, user_id) VALUES
(1, 1),
(2, 1),
(2, 2),
(3, 2),
(3, 3),
(4, 2),
(5, 3);
