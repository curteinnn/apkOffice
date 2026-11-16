CREATE TABLE jenis_tugas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nama VARCHAR(60) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_jenis_tugas_nama UNIQUE (nama)
);

INSERT INTO jenis_tugas (nama) VALUES
    ('Instalasi'),
    ('Maintenance'),
    ('Perbaikan/Troubleshooting');

ALTER TABLE tugas
    ADD COLUMN jenis_tugas_id BIGINT NULL,
    ADD CONSTRAINT fk_tugas_jenis_tugas
        FOREIGN KEY (jenis_tugas_id) REFERENCES jenis_tugas (id);
