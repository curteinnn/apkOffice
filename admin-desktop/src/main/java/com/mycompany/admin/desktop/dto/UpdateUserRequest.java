package com.mycompany.admin.desktop.dto;

public record UpdateUserRequest(String username, String namaLengkap, String email,
        Long jabatanId, String role, String status) {}
