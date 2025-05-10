-- password: 1234
INSERT INTO contest_user (first_name, last_name, email, password, role, group_id) VALUES
    ('Иван', 'Петров', 'ivan.petrov@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_TEACHER', NULL),
    ('Анна', 'Сидорова', 'anna.sidorova@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 1),
    ('Дмитрий', 'Иванов', 'dmitry.ivanov@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 1),
    ('Мария', 'Козлова', 'maria.kozlova@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 2),
    ('Алексей', 'Николаев', 'alexey.nikolaev@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 2),
    ('Екатерина', 'Федорова', 'ekaterina.fedorova@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 3),
    ('Сергей', 'Михайлов', 'sergey.mikhailov@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 3),
    ('Ольга', 'Васильева', 'olga.vasilyeva@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 4),
    ('Владимир', 'Киселёв', 'vladimir.kiselev@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 4);