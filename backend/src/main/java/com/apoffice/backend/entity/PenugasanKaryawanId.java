package com.apoffice.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PenugasanKaryawanId implements Serializable {
    @Column(name = "tugas_id")
    private String tugasId;

    @Column(name = "karyawan_id")
    private Long karyawanId;

    public PenugasanKaryawanId() {}

    public PenugasanKaryawanId(String tugasId, Long karyawanId) {
        this.tugasId = tugasId;
        this.karyawanId = karyawanId;
    }

    public String getTugasId() { return tugasId; }
    public Long getKaryawanId() { return karyawanId; }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PenugasanKaryawanId that)) return false;
        return Objects.equals(tugasId, that.tugasId) && Objects.equals(karyawanId, that.karyawanId);
    }

    @Override public int hashCode() { return Objects.hash(tugasId, karyawanId); }
}
