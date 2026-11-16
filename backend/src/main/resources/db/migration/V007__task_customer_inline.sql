-- Run manually after V006__move_nominal_to_assignment.sql.
-- Customer identity/location become task-level data. No database is accessed here.
-- Task rows without pelanggan_id keep nama_pelanggan NULL; new API requests require it.

ALTER TABLE tugas
    ADD COLUMN nama_pelanggan VARCHAR(150) NULL;

-- Preserve the old customer name and fill task location/map only when task values
-- were empty. Existing task-level values take precedence.
UPDATE tugas t
JOIN pelanggan p ON p.id = t.pelanggan_id
SET t.nama_pelanggan = p.nama,
    t.lokasi = COALESCE(NULLIF(TRIM(t.lokasi), ''), p.alamat),
    t.link_gmaps = COALESCE(NULLIF(TRIM(t.link_gmaps), ''), p.link_gmaps)
WHERE t.nama_pelanggan IS NULL
   OR t.lokasi IS NULL OR TRIM(t.lokasi) = ''
   OR t.link_gmaps IS NULL OR TRIM(t.link_gmaps) = '';

-- Remove the old relationship after copying values to tugas. This drops the
-- old customer master and any customer-only records not referenced by a task.
ALTER TABLE tugas
    DROP FOREIGN KEY fk_tugas_pelanggan,
    DROP INDEX fk_tugas_pelanggan,
    DROP COLUMN pelanggan_id;

DROP TABLE pelanggan;
