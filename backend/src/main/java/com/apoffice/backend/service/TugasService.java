
package com.apoffice.backend.service;

import com.apoffice.backend.dto.CreateTugasRequest;
import com.apoffice.backend.dto.VerifyTugasRequest;
import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.entity.Visit;
import com.apoffice.backend.repository.TugasRepository;
import com.apoffice.backend.repository.UserRepository;
import com.apoffice.backend.repository.VisitRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TugasService {

    private final TugasRepository tugasRepository;
    private final UserRepository userRepository;
    private final VisitRepository visitRepository;

    public TugasService(
            TugasRepository tugasRepository,
            UserRepository userRepository,
            VisitRepository visitRepository
    ) {
        this.tugasRepository = tugasRepository;
        this.userRepository = userRepository;
        this.visitRepository = visitRepository;
    }

    public List<Tugas> getAllTugas(String username, String role) {
        if ("ADMIN".equals(role)) {
            return tugasRepository.findAll();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User tidak ditemukan"
                ));

        return tugasRepository.findByUser(user);
    }

    @Transactional
    public Tugas createTugas(CreateTugasRequest request) {

        if (request.getUserId() == null
                || request.getJudul() == null
                || request.getJudul().isBlank()
                || request.getLokasi() == null
                || request.getLokasi().isBlank()
                || request.getTanggal() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Karyawan, judul, lokasi, dan tanggal wajib diisi"
            );
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Karyawan tidak ditemukan"
                ));

        if (user.getRole() != User.Role.EMPLOYEE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tugas hanya bisa diberikan kepada employee"
            );
        }

        Tugas tugas = new Tugas();
        tugas.setUser(user);
        tugas.setVisit(null);
        tugas.setJudul(request.getJudul().trim());
        tugas.setLokasi(request.getLokasi().trim());
        tugas.setDeskripsi(request.getDeskripsi());
        tugas.setTanggal(request.getTanggal());
        tugas.setStatus("ASSIGNED");
        tugas.setNominal(null);

        LocalDateTime now = LocalDateTime.now();
        tugas.setCreatedAt(now);
        tugas.setUpdatedAt(now);

        return tugasRepository.save(tugas);
    }

    @Transactional
    public Tugas submitTugas(Long id, String username) {
        Tugas tugas = tugasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tugas tidak ditemukan"
                ));

        if (!tugas.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Kamu tidak memiliki akses ke tugas ini"
            );
        }

        if (!"ASSIGNED".equals(tugas.getStatus())
                && !"REJECTED".equals(tugas.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tugas tidak dapat dikirim dengan status saat ini"
            );
        }

        tugas.setStatus("SUBMITTED");
        tugas.setUpdatedAt(LocalDateTime.now());

        return tugasRepository.save(tugas);
    }

    @Transactional
    public Tugas verifyTugas(Long id, VerifyTugasRequest request) {
        Tugas tugas = tugasRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tugas tidak ditemukan"
                ));

        String status = request.getStatus();

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status harus APPROVED atau REJECTED"
            );
        }

        if (!"SUBMITTED".equals(tugas.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Admin hanya bisa memeriksa tugas yang sudah dikirim"
            );
        }

        if ("APPROVED".equals(status)) {
            if (request.getNominal() == null
                    || request.getNominal().signum() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Nominal wajib diisi dan tidak boleh negatif"
                );
            }

            tugas.setNominal(request.getNominal());
        } else {
            tugas.setNominal(null);
        }

        tugas.setStatus(status);
        tugas.setUpdatedAt(LocalDateTime.now());

        return tugasRepository.save(tugas);
    }
}
