package com.mycompany.admin.desktop.dto;

public record CreateTaskRequest(long userId, String jenisTugasId, long pelangganId, String judul, String lokasi,
        String deskripsi, String tanggal, String linkGmaps) {}
