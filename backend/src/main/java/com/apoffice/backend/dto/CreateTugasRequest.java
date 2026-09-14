package com.apoffice.backend.dto;

public class CreateTugasRequest {

    private String judul;
    private String deskripsi;
    private String fotoBukti;

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFotoBukti() {
        return fotoBukti;
    }

    public void setFotoBukti(String fotoBukti) {
        this.fotoBukti = fotoBukti;
    }
}