package com.mycompany.admin.desktop.dto;

public record AttendanceDto(long id, UserDto user, String tanggal, String jamMasuk,
        String jamKeluar, String status, String foto) {}
