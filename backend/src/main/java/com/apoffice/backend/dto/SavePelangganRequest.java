package com.apoffice.backend.dto;

public class SavePelangganRequest {
    private String nama;
    private String namaKontak;
    private String telepon;
    private String alamat;
    private String linkGmaps;

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
