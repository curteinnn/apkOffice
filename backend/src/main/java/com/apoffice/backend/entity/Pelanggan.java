package com.apoffice.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pelanggan")
public class Pelanggan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nama;

    @Column(name = "nama_kontak", length = 100)
    private String namaKontak;

    @Column(length = 30)
    private String telepon;

    @Column(nullable = false, length = 255)
    private String alamat;

    @Column(name = "link_gmaps", columnDefinition = "TEXT")
    private String linkGmaps;

    public Long getId() { return id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNamaKontak() { return namaKontak; }
    public void setNamaKontak(String namaKontak) { this.namaKontak = namaKontak; }
    public String getTelepon() { return telepon; }
    public void setTelepon(String telepon) { this.telepon = telepon; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public String getLinkGmaps() { return linkGmaps; }
    public void setLinkGmaps(String linkGmaps) { this.linkGmaps = linkGmaps; }
}
