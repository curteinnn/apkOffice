package com.apoffice.backend.dto;

import com.apoffice.backend.entity.User;

public class LoginResponse {

    private Long id;
    private String username;
    private String namaLengkap;
    private String email;
    private String jabatan;
    private User.Role role;
    private String foto;
    private User.Status status;
    private String token;

    public LoginResponse(User user, String token) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.namaLengkap = user.getNamaLengkap();
        this.email = user.getEmail();
        this.jabatan = user.getJabatan();
        this.role = user.getRole();
        this.foto = user.getFoto();
        this.status = user.getStatus();
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public String getJabatan() {
        return jabatan;
    }

    public User.Role getRole() {
        return role;
    }

    public String getFoto() {
        return foto;
    }

    public User.Status getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}