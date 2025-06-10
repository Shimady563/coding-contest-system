-- password: 1234
INSERT INTO contest_user (id, first_name, last_name, email, password, role, group_id) VALUES
    (nextval('contest_user_id_seq'), 'Иван', 'Петров', 'ivan.petrov@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_TEACHER', NULL),
    (nextval('contest_user_id_seq'), 'Анна', 'Сидорова', 'anna.sidorova@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 1),
    (nextval('contest_user_id_seq'), 'Дмитрий', 'Иванов', 'dmitry.ivanov@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 1),
    (nextval('contest_user_id_seq'), 'Мария', 'Козлова', 'maria.kozlova@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 2),
    (nextval('contest_user_id_seq'), 'Алексей', 'Николаев', 'alexey.nikolaev@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 2),
    (nextval('contest_user_id_seq'), 'Екатерина', 'Федорова', 'ekaterina.fedorova@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 3),
    (nextval('contest_user_id_seq'), 'Сергей', 'Михайлов', 'sergey.mikhailov@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 3),
    (nextval('contest_user_id_seq'), 'Ольга', 'Васильева', 'olga.vasilyeva@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 4),
    (nextval('contest_user_id_seq'), 'Владимир', 'Киселёв', 'vladimir.kiselev@example.com', '$2a$12$3tt.OCJyy7Fup9XIQGLWmO9qjFcGd.xSdQ5qUcI6qKbdcfq9LCnve', 'ROLE_STUDENT', 4);
