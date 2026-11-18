package com.mycompany.admin.desktop.auth;

public final class AuthResult {
    public final long id;
    public final String username;
    public final String namaLengkap;
    public final String role;
    public final String token;

    public AuthResult(long id, String username, String namaLengkap, String role, String token) {
        this.id = id;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.role = role;
        this.token = token;
    }
}
