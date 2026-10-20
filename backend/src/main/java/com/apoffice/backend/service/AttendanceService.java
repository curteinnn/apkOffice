package com.apoffice.backend.service;

import com.apoffice.backend.entity.Attendance;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.AttendanceRepository;
import com.apoffice.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private static final ZoneId ZONA_WIB =
            ZoneId.of("Asia/Jakarta");

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            UserRepository userRepository
    ) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance checkIn(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User tidak ditemukan"
                ));

        LocalDateTime sekarang =
                LocalDateTime.now(ZONA_WIB);

        LocalDate hariIni = sekarang.toLocalDate();

        Optional<Attendance> existingAttendance =
                attendanceRepository.findByUserAndTanggal(
                        user,
                        hariIni
                );

        if (existingAttendance.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Anda sudah melakukan check-in hari ini"
            );
        }

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setTanggal(hariIni);
        attendance.setJamMasuk(sekarang);
        attendance.setStatus("PRESENT");

        return attendanceRepository.save(attendance);
    }

    public Attendance checkOut(String username) {
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
                        HttpStatus.BAD_REQUEST,
                        "Anda belum melakukan check-in hari ini"
                ));

        if (attendance.getJamKeluar() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Anda sudah melakukan check-out hari ini"
            );
        }

        LocalDateTime sekarang =
                LocalDateTime.now(ZONA_WIB);

        attendance.setJamKeluar(sekarang);

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getMyAttendance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User tidak ditemukan"
                ));

        return attendanceRepository
                .findByUserOrderByTanggalDesc(user);
    }
}