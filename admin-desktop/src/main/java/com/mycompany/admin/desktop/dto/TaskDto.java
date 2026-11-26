package com.mycompany.admin.desktop.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public record TaskDto(String id, List<UserDto> assignees, JenisTugasDto jenisTugas,
        String namaPelanggan, String judul, String lokasi,
        String linkGmaps, String deskripsi, String status, Map<Long, BigDecimal> nominalByAssignee,
        String tanggal, String createdAt, String updatedAt, String alasanPenolakan) {}
