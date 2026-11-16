package com.mycompany.admin.desktop.dto;

public record PelangganDto(long id, String nama, String namaKontak, String telepon,
        String alamat, String linkGmaps) {
    @Override public String toString() { return nama; }
}
