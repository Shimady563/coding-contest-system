INSERT INTO solution (id, code, status, submitted_at, tests_passed, task_id, user_id) VALUES
    (nextval('solution_id_seq'), 'int main() { return 0; }', 'ACCEPTED', '2025-03-16 10:15:00', 5, 1, 2),
    (nextval('solution_id_seq'), 'print("Hello, world!")', 'ACCEPTED', '2025-03-16 11:00:00', 3, 2, 2),
    (nextval('solution_id_seq'), 'int a[1000000]; int main() { return 0; }', 'TIMED_OUT', '2025-03-16 12:30:00', 2, 3, 3),
    (nextval('solution_id_seq'), 'int main() { return 1 / 0; }', 'RUNTIME_ERROR', '2025-03-16 13:45:00', 0, 4, 3),
    (nextval('solution_id_seq'), 'int main() { unknown_function(); }', 'COMPILE_ERROR', '2025-03-16 14:00:00', 0, 5, 4),
    (nextval('solution_id_seq'), 'def f(): return 42', 'ACCEPTED', '2025-03-16 14:30:00', 4, 6, 4),
    (nextval('solution_id_seq'), 'while True: pass', 'TIMED_OUT', '2025-03-16 15:00:00', 1, 7, 5),
    (nextval('solution_id_seq'), 'int main() { int x; cin >> x; cout << x; return 0; }', 'WRONG_ANSWER', '2025-03-16 15:30:00', 3, 8, 5),
    (nextval('solution_id_seq'), 'int main() { return 42; }', 'ACCEPTED', '2025-03-16 16:00:00', 5, 9, 6),
    (nextval('solution_id_seq'), 'console.log("Hello!");', 'ACCEPTED', '2025-03-16 16:45:00', 4, 10, 6),
    (nextval('solution_id_seq'), 'int main() { return 0; }', 'ACCEPTED', '2025-03-16 10:15:00', 5, 11, 7);
