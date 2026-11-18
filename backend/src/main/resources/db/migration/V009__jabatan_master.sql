-- Jalankan manual setelah V008__string_task_ids.sql.
-- Nilai teks jabatan lama dipetakan ke master dan kolom lamanya tetap disimpan untuk verifikasi.

CREATE TABLE jabatan (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nama VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    nama_normalized VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_jabatan_nama_normalized (nama_normalized)
);

-- Satu master dibuat untuk setiap nama jabatan lama setelah spasi tepi dan kapitalisasi dinormalisasi.
INSERT INTO jabatan (nama, nama_normalized, status)
SELECT MIN(REGEXP_REPLACE(TRIM(jabatan), '[[:space:]]+', ' ')),
       LOWER(REGEXP_REPLACE(TRIM(jabatan), '[[:space:]]+', ' ')) COLLATE utf8mb4_unicode_ci,
       'ACTIVE'
FROM `user`
WHERE jabatan IS NOT NULL AND TRIM(jabatan) <> ''
GROUP BY LOWER(REGEXP_REPLACE(TRIM(jabatan), '[[:space:]]+', ' ')) COLLATE utf8mb4_unicode_ci;

ALTER TABLE `user`
    ADD COLUMN jabatan_id BIGINT NULL;

UPDATE `user` u
JOIN jabatan j
    ON LOWER(REGEXP_REPLACE(TRIM(u.jabatan), '[[:space:]]+', ' ')) COLLATE utf8mb4_unicode_ci
        = j.nama_normalized COLLATE utf8mb4_unicode_ci
SET u.jabatan_id = j.id
WHERE u.jabatan IS NOT NULL AND TRIM(u.jabatan) <> '';

ALTER TABLE `user`
    ADD INDEX idx_user_jabatan_id (jabatan_id),
    ADD CONSTRAINT fk_user_jabatan
        FOREIGN KEY (jabatan_id) REFERENCES jabatan (id)
        ON UPDATE RESTRICT ON DELETE RESTRICT;

-- Verifikasi manual setelah migrasi: hasil query berikut seharusnya 0 baris.
-- SELECT id, username, jabatan FROM `user`
-- WHERE jabatan IS NOT NULL AND TRIM(jabatan) <> '' AND jabatan_id IS NULL;
-- Jangan jalankan V010 sebelum query verifikasi di atas menghasilkan 0 baris.
