package com.mycompany.admin.desktop.dto;

public record LoginResponse(long id, String username, String namaLengkap, String email,
        String jabatan, String role, String foto, String status, String token) {}
