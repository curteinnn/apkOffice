package com.mycompany.admin.desktop.dto;

public record CreateUserRequest(String username, String password, String namaLengkap,
        String email, String jabatan, String role) {}
