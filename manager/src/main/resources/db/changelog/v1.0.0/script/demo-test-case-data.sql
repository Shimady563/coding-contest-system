INSERT INTO test_case (id, input, output, task_id) VALUES
    (nextval('test_case_id_seq'), '2 3', '5', 1),
    (nextval('test_case_id_seq'), '10 20', '30', 1),
    (nextval('test_case_id_seq'), '5 7', '12', 1),

    (nextval('test_case_id_seq'), '5 3 8 1 2', '1 2 3 5 8', 2),
    (nextval('test_case_id_seq'), '9 4 6 2 1', '1 2 4 6 9', 2),

    (nextval('test_case_id_seq'), '36 48', '12', 3),
    (nextval('test_case_id_seq'), '100 75', '25', 3),

    (nextval('test_case_id_seq'), '5 push 3 push 2 pop', '3', 4),
    (nextval('test_case_id_seq'), 'enqueue 10 enqueue 20 dequeue', '10', 4),

    (nextval('test_case_id_seq'), '7', 'true', 5),
    (nextval('test_case_id_seq'), '10', 'false', 5),
    (nextval('test_case_id_seq'), '17', 'true', 5),

    (nextval('test_case_id_seq'), '5 10 15 20 25; 15', 'Found at index 2', 6),
    (nextval('test_case_id_seq'), '1 3 5 7 9; 4', 'Not found', 6),

    (nextval('test_case_id_seq'), 'push 5 push 10 pop', '10', 7),
    (nextval('test_case_id_seq'), 'push 7 push 8 pop', '8', 7),

    (nextval('test_case_id_seq'), '5', '120', 8),
    (nextval('test_case_id_seq'), '7', '5040', 8),

    (nextval('test_case_id_seq'), '10 2 8 4 6', '2 4 6 8 10', 9),
    (nextval('test_case_id_seq'), '9 7 5 3 1', '1 3 5 7 9', 9),

    (nextval('test_case_id_seq'), '2 3', '-1', 10),
    (nextval('test_case_id_seq'), '10 20', '-10', 10),
    (nextval('test_case_id_seq'), '5 7', '-2', 10),

    (nextval('test_case_id_seq'), '5 3 8 1 2', '1 2 3 5 8', 11),
    (nextval('test_case_id_seq'), '9 4 6 2 1', '1 2 4 6 9', 11);
