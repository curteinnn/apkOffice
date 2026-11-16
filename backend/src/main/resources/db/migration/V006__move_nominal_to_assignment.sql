-- Run manually after V005__shared_tugas_assignees.sql.
-- Preserve the existing task nominal for its original employee assignment.
ALTER TABLE penugasan_karyawan
    ADD COLUMN nominal DECIMAL(12,2) NULL;

UPDATE penugasan_karyawan pk
JOIN tugas t ON t.id = pk.tugas_id AND t.user_id = pk.karyawan_id
SET pk.nominal = t.nominal;

-- The existing schema dump identifies the FK and supporting index as fk_tugas_user.
ALTER TABLE tugas
    DROP FOREIGN KEY fk_tugas_user,
    DROP INDEX fk_tugas_user,
    DROP COLUMN user_id,
    DROP COLUMN nominal;
