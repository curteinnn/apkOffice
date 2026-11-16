package com.mycompany.admin.desktop.dto;

import java.math.BigDecimal;

public record TaskDto(long id, UserDto user, JenisTugasDto jenisTugas, PelangganDto pelanggan, String judul, String lokasi,
        String linkGmaps, String deskripsi, String status, BigDecimal nominal,
        String tanggal, String createdAt, String updatedAt) {}
