package com.mycompany.admin.desktop.dto;

public record UserDto(long id, String username, String namaLengkap, String email,
        String jabatan, Long jabatanId, String role, String foto, String status) {}
