-- Run manually after V007__task_customer_inline.sql.
-- Converts the task primary key and assignment foreign key to matching VARCHARs.
-- Existing numeric task IDs, if any, are retained as text during conversion.

ALTER TABLE penugasan_karyawan
    DROP FOREIGN KEY fk_penugasan_karyawan_tugas;

ALTER TABLE tugas
    MODIFY COLUMN id VARCHAR(32) NOT NULL;

ALTER TABLE penugasan_karyawan
    MODIFY COLUMN tugas_id VARCHAR(32) NOT NULL;

ALTER TABLE penugasan_karyawan
    ADD CONSTRAINT fk_penugasan_karyawan_tugas
        FOREIGN KEY (tugas_id) REFERENCES tugas (id) ON DELETE CASCADE;

CREATE TABLE tugas_id_sequence (
    tahun SMALLINT NOT NULL,
    nomor_terakhir BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (tahun)
);

-- Continue the sequence if existing task IDs already use the new format.
INSERT INTO tugas_id_sequence (tahun, nomor_terakhir)
SELECT
    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(id, '-', 2), '-', -1) AS UNSIGNED),
    MAX(CAST(SUBSTRING_INDEX(id, '-', -1) AS UNSIGNED))
FROM tugas
WHERE id REGEXP '^TASK-[0-9]{4}-[0-9]+$'
GROUP BY CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(id, '-', 2), '-', -1) AS UNSIGNED)
ON DUPLICATE KEY UPDATE nomor_terakhir = GREATEST(nomor_terakhir, VALUES(nomor_terakhir));
