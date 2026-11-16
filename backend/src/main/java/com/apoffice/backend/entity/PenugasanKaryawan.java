package com.apoffice.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "penugasan_karyawan")
public class PenugasanKaryawan {
    @EmbeddedId
    private PenugasanKaryawanId id = new PenugasanKaryawanId();

    @MapsId("tugasId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tugas_id", nullable = false)
    @JsonIgnore
    private Tugas tugas;

    @MapsId("karyawanId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "karyawan_id", nullable = false)
    private User karyawan;

    @Column(precision = 12, scale = 2)
    private BigDecimal nominal;

    public PenugasanKaryawan() {}

    public PenugasanKaryawan(Tugas tugas, User karyawan) {
        this.tugas = tugas;
        this.karyawan = karyawan;
        this.id = new PenugasanKaryawanId(tugas.getId(), karyawan.getId());
    }

    public PenugasanKaryawanId getId() { return id; }
    public Tugas getTugas() { return tugas; }
    public void setTugas(Tugas tugas) { this.tugas = tugas; }
    public User getKaryawan() { return karyawan; }
    public void setKaryawan(User karyawan) { this.karyawan = karyawan; }
    public BigDecimal getNominal() { return nominal; }
    public void setNominal(BigDecimal nominal) { this.nominal = nominal; }
}
