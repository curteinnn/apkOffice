package com.apoffice.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "nama_lengkap", nullable = false, length = 100)
    private String namaLengkap;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String jabatan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 255)
    private String foto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Role {
        ADMIN,
        EMPLOYEE
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    // getter & setter 
    public Long getId() {
    return id;
}

public String getUsername() {
    return username;
}

public String getPassword() {
    return password;
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

public Role getRole() {
    return role;
}

public String getFoto() {
    return foto;
}

public Status getStatus() {
    return status;
}

public void setUsername(String username) {
    this.username = username;
}

public void setPassword(String password) {
    this.password = password;
}

public void setNamaLengkap(String namaLengkap) {
    this.namaLengkap = namaLengkap;
}

public void setEmail(String email) {
    this.email = email;
}

public void setJabatan(String jabatan) {
    this.jabatan = jabatan;
}

public void setRole(Role role) {
    this.role = role;
}

public void setStatus(Status status) {
    this.status = status;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
}
}