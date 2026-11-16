package com.apoffice.backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TugasIdGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.MANDATORY)
    public String nextId(LocalDate tanggalTugas) {
        int tahun = tanggalTugas.getYear();

        entityManager.createNativeQuery("""
                INSERT INTO tugas_id_sequence (tahun, nomor_terakhir)
                VALUES (:tahun, 0)
                ON DUPLICATE KEY UPDATE tahun = VALUES(tahun)
                """)
                .setParameter("tahun", tahun)
                .executeUpdate();

        Number lastNumber = (Number) entityManager.createNativeQuery(
                "SELECT nomor_terakhir FROM tugas_id_sequence WHERE tahun = :tahun FOR UPDATE")
                .setParameter("tahun", tahun)
                .getSingleResult();

        long nextNumber = lastNumber.longValue() + 1;
        entityManager.createNativeQuery(
                "UPDATE tugas_id_sequence SET nomor_terakhir = :nomor WHERE tahun = :tahun")
                .setParameter("nomor", nextNumber)
                .setParameter("tahun", tahun)
                .executeUpdate();

        return String.format(Locale.ROOT, "TASK-%d-%04d", tahun, nextNumber);
    }
}
