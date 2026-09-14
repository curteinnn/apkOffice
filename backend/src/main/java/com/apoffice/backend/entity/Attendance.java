package com.apoffice.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate tanggal;

    @Column(name = "jam_masuk", nullable = false)
    private LocalDateTime jamMasuk;
    
    @Column(name = "jam_keluar")
    private LocalDateTime jamKeluar;
    
    public LocalDateTime getJamKeluar() {
    return jamKeluar;
    }

    public void setJamKeluar(LocalDateTime jamKeluar) {
        this.jamKeluar = jamKeluar;
    }

    @Column(nullable = false, length = 20)
    private String status;

    @Column(length = 255)
    private String foto;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalDateTime getJamMasuk() {
        return jamMasuk;
    }

    public void setJamMasuk(LocalDateTime jamMasuk) {
        this.jamMasuk = jamMasuk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}