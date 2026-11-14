package com.mycompany.admin.desktop.dto;

import java.math.BigDecimal;

public record TaskDto(long id, UserDto user, String judul, String lokasi,
        String linkGmaps, String deskripsi, String status, BigDecimal nominal,
        String tanggal, String createdAt, String updatedAt) {}
