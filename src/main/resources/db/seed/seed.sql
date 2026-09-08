-- ============================================================
-- Seed de demarrage — VOGT HIGH TECH Digital Campus
-- Ne contient AUCUNE donnee institutionnelle officielle
-- (frais, dates, formations reelles). A completer/valider
-- par l'administration avant toute mise en production.
-- ============================================================

-- Annee academique active par defaut (a ajuster depuis /admin/academic-years)
INSERT INTO academic_years (id, created_at, updated_at, deleted, label, start_date, end_date, status)
VALUES (gen_random_uuid(), now(), now(), false, '2026-2027', '2026-09-01', '2027-07-31', 'ACTIVE');

-- Compte SUPER_ADMIN de demarrage — mot de passe a changer immediatement.
-- Hash bcrypt correspondant a "ChangeMe123!" (a regenerer avant toute mise en prod reelle)
INSERT INTO users (id, created_at, updated_at, deleted, email, password_hash, first_name, last_name, phone, role, active, two_factor_enabled)
VALUES (gen_random_uuid(), now(), now(), false, 'admin@vogthightech.cm',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5W4jTV5jV1jH0kX0oW2b0G6D1uK3e',
        'Super', 'Admin', '+237600000000', 'SUPER_ADMIN', true, false);
