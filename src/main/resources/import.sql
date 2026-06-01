-- Seed data for the eager/lazy/N+1 demo
-- Loaded automatically by Quarkus on startup (drop-and-create strategy).

-- Users
INSERT INTO users (id, full_name, email, role, active, firebase_uuid, created_at, updated_at) VALUES
('00000000-0000-0000-0000-000000000001', 'Test User',        'test@test.com',       'USER',    true, 'test-firebase-uid', '2025-01-01 08:00:00', '2025-01-01 08:00:00'),
('11111111-1111-1111-1111-111111111111', 'Ada Lovelace',     'ada@example.com',     'admin',   true, 'seed-firebase-1', '2025-01-01 09:00:00', '2025-01-01 09:00:00'),
('22222222-2222-2222-2222-222222222222', 'Linus Torvalds',   'linus@example.com',   'user',    true, 'seed-firebase-2', '2025-01-02 09:00:00', '2025-01-02 09:00:00'),
('33333333-3333-3333-3333-333333333333', 'Grace Hopper',     'grace@example.com',   'user',    true, 'seed-firebase-3', '2025-01-03 09:00:00', '2025-01-03 09:00:00'),
('44444444-4444-4444-4444-444444444444', 'Dennis Ritchie',   'dennis@example.com',  'user',    true, 'seed-firebase-4', '2025-01-04 09:00:00', '2025-01-04 09:00:00');

-- Categories
INSERT INTO categories (id, name) VALUES
('aaaaaaa1-0000-0000-0000-000000000001', 'Trabajo'),
('aaaaaaa2-0000-0000-0000-000000000002', 'Personal'),
('aaaaaaa3-0000-0000-0000-000000000003', 'Urgente'),
('aaaaaaa4-0000-0000-0000-000000000004', 'Aprendizaje'),
('aaaaaaa5-0000-0000-0000-000000000005', 'Casa');

-- Todos
INSERT INTO todos (id, title, description, completed, created_at, owner_id) VALUES
('bbbbbbb1-0000-0000-0000-000000000001', 'Disenar arquitectura',         'Definir capas de la app',                 false, '2025-02-01 10:00:00', '11111111-1111-1111-1111-111111111111'),
('bbbbbbb2-0000-0000-0000-000000000002', 'Comprar despensa',             'Lista semanal',                            false, '2025-02-02 10:00:00', '22222222-2222-2222-2222-222222222222'),
('bbbbbbb3-0000-0000-0000-000000000003', 'Pagar internet',               'Antes del dia 10',                         true,  '2025-02-03 10:00:00', '22222222-2222-2222-2222-222222222222'),
('bbbbbbb4-0000-0000-0000-000000000004', 'Estudiar JPA',                 'Eager vs Lazy y N+1',                      false, '2025-02-04 10:00:00', '33333333-3333-3333-3333-333333333333'),
('bbbbbbb5-0000-0000-0000-000000000005', 'Refactor del modulo de auth',  'Quitar duplicacion',                       false, '2025-02-05 10:00:00', '11111111-1111-1111-1111-111111111111'),
('bbbbbbb6-0000-0000-0000-000000000006', 'Llamar al doctor',             'Cita de revision',                         false, '2025-02-06 10:00:00', '44444444-4444-4444-4444-444444444444'),
('bbbbbbb7-0000-0000-0000-000000000007', 'Preparar clase',               'Slides de Hibernate',                      false, '2025-02-07 10:00:00', '33333333-3333-3333-3333-333333333333'),
('bbbbbbb8-0000-0000-0000-000000000008', 'Leer paper',                   'On the Criteria to be Used in Decomposing', false, '2025-02-08 10:00:00', '44444444-4444-4444-4444-444444444444');

-- Todo <-> Category (many to many)
INSERT INTO todo_category (todo_id, category_id) VALUES
('bbbbbbb1-0000-0000-0000-000000000001', 'aaaaaaa1-0000-0000-0000-000000000001'),
('bbbbbbb1-0000-0000-0000-000000000001', 'aaaaaaa3-0000-0000-0000-000000000003'),
('bbbbbbb2-0000-0000-0000-000000000002', 'aaaaaaa2-0000-0000-0000-000000000002'),
('bbbbbbb2-0000-0000-0000-000000000002', 'aaaaaaa5-0000-0000-0000-000000000005'),
('bbbbbbb3-0000-0000-0000-000000000003', 'aaaaaaa5-0000-0000-0000-000000000005'),
('bbbbbbb4-0000-0000-0000-000000000004', 'aaaaaaa4-0000-0000-0000-000000000004'),
('bbbbbbb5-0000-0000-0000-000000000005', 'aaaaaaa1-0000-0000-0000-000000000001'),
('bbbbbbb5-0000-0000-0000-000000000005', 'aaaaaaa3-0000-0000-0000-000000000003'),
('bbbbbbb6-0000-0000-0000-000000000006', 'aaaaaaa2-0000-0000-0000-000000000002'),
('bbbbbbb7-0000-0000-0000-000000000007', 'aaaaaaa1-0000-0000-0000-000000000001'),
('bbbbbbb7-0000-0000-0000-000000000007', 'aaaaaaa4-0000-0000-0000-000000000004'),
('bbbbbbb8-0000-0000-0000-000000000008', 'aaaaaaa4-0000-0000-0000-000000000004');

-- Comments
INSERT INTO comments (id, content, author_email, created_at, todo_id) VALUES
('ccccccc1-0000-0000-0000-000000000001', 'Hagamoslo en capas hexagonales',                'linus@example.com', '2025-02-10 10:00:00', 'bbbbbbb1-0000-0000-0000-000000000001'),
('ccccccc2-0000-0000-0000-000000000002', 'Ojo con las dependencias circulares',           'grace@example.com', '2025-02-10 11:00:00', 'bbbbbbb1-0000-0000-0000-000000000001'),
('ccccccc3-0000-0000-0000-000000000003', 'No olvides el detergente',                       'ada@example.com',   '2025-02-11 09:00:00', 'bbbbbbb2-0000-0000-0000-000000000002'),
('ccccccc4-0000-0000-0000-000000000004', 'Ya esta pagado, marcalo como completado',        'linus@example.com', '2025-02-11 18:00:00', 'bbbbbbb3-0000-0000-0000-000000000003'),
('ccccccc5-0000-0000-0000-000000000005', 'Lee el capitulo de fetch strategies primero',    'dennis@example.com','2025-02-12 08:00:00', 'bbbbbbb4-0000-0000-0000-000000000004'),
('ccccccc6-0000-0000-0000-000000000006', 'Tambien revisa el de cache de segundo nivel',    'ada@example.com',   '2025-02-12 12:00:00', 'bbbbbbb4-0000-0000-0000-000000000004'),
('ccccccc7-0000-0000-0000-000000000007', 'Quita el AuthFilter duplicado',                  'grace@example.com', '2025-02-13 14:00:00', 'bbbbbbb5-0000-0000-0000-000000000005'),
('ccccccc8-0000-0000-0000-000000000008', 'Confirmar cita por telefono',                    'ada@example.com',   '2025-02-14 09:00:00', 'bbbbbbb6-0000-0000-0000-000000000006'),
('ccccccc9-0000-0000-0000-000000000009', 'Incluir slide del problema N+1',                 'linus@example.com', '2025-02-14 16:00:00', 'bbbbbbb7-0000-0000-0000-000000000007');
