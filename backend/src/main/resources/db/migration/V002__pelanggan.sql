CREATE TABLE pelanggan (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nama VARCHAR(120) NOT NULL,
    nama_kontak VARCHAR(100) NULL,
    telepon VARCHAR(30) NULL,
    alamat VARCHAR(255) NOT NULL,
    link_gmaps TEXT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE tugas
    ADD COLUMN pelanggan_id BIGINT NULL,
    ADD CONSTRAINT fk_tugas_pelanggan
        FOREIGN KEY (pelanggan_id) REFERENCES pelanggan (id);
