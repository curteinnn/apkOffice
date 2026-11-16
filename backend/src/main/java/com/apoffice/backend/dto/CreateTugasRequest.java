package com.apoffice.backend.dto;

import java.time.LocalDate;

public class CreateTugasRequest {

    private Long userId;
    private String jenisTugasId;
    private String namaPelanggan;
    private String judul;
    private String lokasi;
    private String deskripsi;
    private LocalDate tanggal;
    private String linkGmaps;

    public Long getUserId() {
        return userId;
    }

    public String getJenisTugasId() { return jenisTugasId; }

    public void setJenisTugasId(String jenisTugasId) { this.jenisTugasId = jenisTugasId; }

    public String getNamaPelanggan() { return namaPelanggan; }

    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public String getLinkGmaps() { return linkGmaps; }
    public void setLinkGmaps(String linkGmaps) { this.linkGmaps = linkGmaps; }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }
}
