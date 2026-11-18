package com.apoffice.backend.dto;

import java.time.LocalDate;
import java.util.List;

public class UpdateTugasRequest {
    private List<Long> userIds;
    private String jenisTugasId;
    private String namaPelanggan;
    private String judul;
    private String lokasi;
    private String deskripsi;
    private LocalDate tanggal;
    private String linkGmaps;

    public List<Long> getUserIds() { return userIds; }
    public void setUserIds(List<Long> userIds) { this.userIds = userIds; }
    public String getJenisTugasId() { return jenisTugasId; }
    public void setJenisTugasId(String jenisTugasId) { this.jenisTugasId = jenisTugasId; }
    public String getNamaPelanggan() { return namaPelanggan; }
    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }
    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }
    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public String getLinkGmaps() { return linkGmaps; }
    public void setLinkGmaps(String linkGmaps) { this.linkGmaps = linkGmaps; }
}
