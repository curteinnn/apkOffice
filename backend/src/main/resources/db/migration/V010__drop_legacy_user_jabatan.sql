-- Jalankan manual hanya setelah V009 selesai dan hasil verifikasi berikut 0.
-- SELECT COUNT(*) AS jabatan_tidak_terpetakan FROM `user`
-- WHERE jabatan IS NOT NULL AND TRIM(jabatan) <> '' AND jabatan_id IS NULL;
-- Pastikan aplikasi baru sudah membaca user.jabatan melalui FK jabatan_id sebelum melanjutkan.

ALTER TABLE `user`
    DROP COLUMN jabatan;
