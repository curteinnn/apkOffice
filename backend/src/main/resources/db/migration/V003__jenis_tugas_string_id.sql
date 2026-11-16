-- Run after V001__jenis_tugas.sql. This preserves task-to-type references.
ALTER TABLE tugas
    DROP FOREIGN KEY fk_tugas_jenis_tugas;

ALTER TABLE tugas
    MODIFY COLUMN jenis_tugas_id VARCHAR(20) NULL;

ALTER TABLE jenis_tugas
    ADD COLUMN kode VARCHAR(20) NULL;

UPDATE jenis_tugas
SET kode = CASE nama
    WHEN 'Instalasi' THEN 'INT-001'
    WHEN 'Maintenance' THEN 'MNT-001'
    WHEN 'Perbaikan/Troubleshooting' THEN 'TRB-001'
    ELSE NULL
END;

UPDATE tugas AS t
JOIN jenis_tugas AS jt ON t.jenis_tugas_id = CAST(jt.id AS CHAR)
SET t.jenis_tugas_id = jt.kode;

ALTER TABLE jenis_tugas
    DROP PRIMARY KEY,
    MODIFY COLUMN id VARCHAR(20) NOT NULL;

UPDATE jenis_tugas
SET id = kode;

ALTER TABLE jenis_tugas
    DROP COLUMN kode,
    ADD PRIMARY KEY (id);

ALTER TABLE tugas
    ADD CONSTRAINT fk_tugas_jenis_tugas
        FOREIGN KEY (jenis_tugas_id) REFERENCES jenis_tugas (id);
