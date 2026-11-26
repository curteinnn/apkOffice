package com.apoffice.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tugas")
public class Tugas {

    @Id
    @Column(length = 32)
    private String id;

    @OneToMany(mappedBy = "tugas", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PenugasanKaryawan> penugasanKaryawan = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jenis_tugas_id")
    private JenisTugas jenisTugas;

    @Column(name = "nama_pelanggan", length = 150)
    private String namaPelanggan;

    @Column(nullable = false, length = 150)
    private String judul;

    @Column(length = 255)
    private String lokasi;

    @Column(name = "link_gmaps", columnDefinition = "TEXT")
    private String linkGmaps;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String alasanPenolakan;

    @Column(nullable = false)
    private LocalDate tanggal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    @JsonIgnore
    public List<PenugasanKaryawan> getPenugasanKaryawan() { return penugasanKaryawan; }

    public void setAssignees(Set<User> assignees) {
        this.penugasanKaryawan.clear();
        assignees.forEach(user -> this.penugasanKaryawan.add(new PenugasanKaryawan(this, user)));
    }

    public List<User> getAssignees() {
        return penugasanKaryawan.stream().map(PenugasanKaryawan::getKaryawan).toList();
    }

    public Map<Long, java.math.BigDecimal> getNominalByAssignee() {
        Map<Long, java.math.BigDecimal> nominal = new LinkedHashMap<>();
        penugasanKaryawan.forEach(assignment -> nominal.put(
                assignment.getKaryawan().getId(), assignment.getNominal()));
        return nominal;
    }

    public JenisTugas getJenisTugas() { return jenisTugas; }

    public void setJenisTugas(JenisTugas jenisTugas) { this.jenisTugas = jenisTugas; }

    public String getNamaPelanggan() { return namaPelanggan; }

    public void setNamaPelanggan(String namaPelanggan) { this.namaPelanggan = namaPelanggan; }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getLinkGmaps() {
        return linkGmaps;
    }

    public void setLinkGmaps(String linkGmaps) {
    this.linkGmaps = linkGmaps;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
