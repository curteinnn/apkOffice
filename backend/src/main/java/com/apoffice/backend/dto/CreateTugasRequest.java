package com.apoffice.backend.dto;

import org.springframework.web.multipart.MultipartFile;

public class CreateTugasRequest {

    private String judul;
    private String deskripsi;
    private MultipartFile foto;

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

    public MultipartFile getFoto() {
        return foto;
    }

    public void setFoto(MultipartFile foto) {
        this.foto = foto;
    }
}