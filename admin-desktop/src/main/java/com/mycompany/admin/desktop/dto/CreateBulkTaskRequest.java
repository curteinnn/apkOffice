package com.mycompany.admin.desktop.dto;

import java.util.List;

public record CreateBulkTaskRequest(List<Long> userIds, String jenisTugasId, String namaPelanggan,
        String judul, String lokasi, String deskripsi, String tanggal, String linkGmaps) {}
