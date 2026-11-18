package com.apoffice.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jenis_tugas")
public class JenisTugas {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, unique = true, length = 60)
    private String nama;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
}
