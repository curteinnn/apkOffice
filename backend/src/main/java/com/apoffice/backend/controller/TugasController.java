package com.apoffice.backend.controller;

import com.apoffice.backend.dto.CreateTugasRequest;
import com.apoffice.backend.dto.VerifyTugasRequest;
import com.apoffice.backend.entity.Attendance;
import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.AttendanceRepository;
import com.apoffice.backend.repository.UserRepository;
import com.apoffice.backend.service.TugasService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/tugas")
public class TugasController {

    private static final ZoneId ZONA_WIB =
            ZoneId.of("Asia/Jakarta");

    private final TugasService tugasService;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public TugasController(
            TugasService tugasService,
            AttendanceRepository attendanceRepository,
            UserRepository userRepository
    ) {
        this.tugasService = tugasService;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Tugas> getAllTugas(HttpServletRequest httpRequest) {
        String username =
                (String) httpRequest.getAttribute("username");
        String role =
                (String) httpRequest.getAttribute("role");

        if ("EMPLOYEE".equals(role)) {
            if (username == null || username.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Token login tidak valid"
                );
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User tidak ditemukan"
                    ));

            LocalDate hariIni =
                    LocalDate.now(ZONA_WIB);

            Attendance attendance = attendanceRepository
                    .findByUserAndTanggal(user, hariIni)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Silakan check-in terlebih dahulu untuk mengakses List Visit"
                    ));

            if (attendance.getJamKeluar() != null) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Anda sudah check-out hari ini"
                );
            }
        }

        return tugasService.getAllTugas(username, role);
    }

    @PostMapping
    public Tugas createTugas(
            @RequestBody CreateTugasRequest request,
            HttpServletRequest httpRequest
    ) {
        String role =
                (String) httpRequest.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya admin yang dapat membuat tugas"
            );
        }

        return tugasService.createTugas(request);
    }

    @PutMapping("/{id}/submit")
    public Tugas submitTugas(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        String username =
                (String) httpRequest.getAttribute("username");
        String role =
                (String) httpRequest.getAttribute("role");

        if (!"EMPLOYEE".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya karyawan yang dapat mengirim tugas"
            );
        }

        return tugasService.submitTugas(id, username);
    }

    @PutMapping("/{id}/verify")
    public Tugas verifyTugas(
            @PathVariable Long id,
            @RequestBody VerifyTugasRequest request,
            HttpServletRequest httpRequest
    ) {
        String role =
                (String) httpRequest.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya admin yang dapat memverifikasi tugas"
            );
        }

        return tugasService.verifyTugas(id, request);
    }

    @PutMapping("/{id}/start")
    public Tugas startTugas(
            @PathVariable Long id,
            HttpServletRequest httpRequest
    ) {
        String username =
                (String) httpRequest.getAttribute("username");
        String role =
                (String) httpRequest.getAttribute("role");

        if (!"EMPLOYEE".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya karyawan yang dapat memulai tugas"
            );
        }

        return tugasService.startTugas(id, username);
    }
}