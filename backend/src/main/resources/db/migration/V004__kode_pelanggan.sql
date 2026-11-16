-- Run after V002__pelanggan.sql. This does not change the numeric primary key.
ALTER TABLE pelanggan
    ADD COLUMN kode_pelanggan VARCHAR(20) NULL;

UPDATE pelanggan
SET kode_pelanggan = CONCAT(
    'CUS-', YEAR(CURRENT_DATE), '-', LPAD(id, 4, '0')
);

ALTER TABLE pelanggan
    ADD CONSTRAINT uk_pelanggan_kode UNIQUE (kode_pelanggan);
