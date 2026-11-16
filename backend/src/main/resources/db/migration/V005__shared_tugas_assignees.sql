-- Run manually after the existing tugas and user tables are present.
CREATE TABLE penugasan_karyawan (
    tugas_id BIGINT UNSIGNED NOT NULL,
    karyawan_id BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (tugas_id, karyawan_id),
    CONSTRAINT fk_penugasan_karyawan_tugas
        FOREIGN KEY (tugas_id) REFERENCES tugas (id) ON DELETE CASCADE,
    CONSTRAINT fk_penugasan_karyawan_user
        FOREIGN KEY (karyawan_id) REFERENCES `user` (id)
);

-- Preserve each existing task's current employee assignment.
INSERT INTO penugasan_karyawan (tugas_id, karyawan_id)
SELECT id, user_id FROM tugas;
