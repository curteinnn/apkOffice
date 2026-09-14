package com.apoffice.backend.service;

import com.apoffice.backend.entity.Attendance;
import com.apoffice.backend.repository.AttendanceRepository;
import org.springframework.stereotype.Service;

import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;

@Service
public class AttendanceService {

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
            .orElseThrow(() ->
                    
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User tidak ditemukan"
                        )
                    );
    
             Optional<Attendance> existingAttendance =
                attendanceRepository.findByUserAndTanggal(user, LocalDate.now());
                
             System.out.println("ATTENDANCE SUDAH ADA: " + existingAttendance.isPresent());
               if (existingAttendance.isPresent()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Anda sudah melakukan check-in hari ini"
                    );
                }

            Attendance attendance = new Attendance();

            attendance.setUser(user);
            attendance.setTanggal(LocalDate.now());
            attendance.setJamMasuk(LocalDateTime.now());
            attendance.setStatus("PRESENT");

            return attendanceRepository.save(attendance);
    }
    
    public Attendance checkOut(String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "User tidak ditemukan"
                    )
            );

    Attendance attendance = attendanceRepository
            .findByUserAndTanggal(user, LocalDate.now())
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Anda belum melakukan check-in hari ini"
                    )
            );

    if (attendance.getJamKeluar() != null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Anda sudah melakukan check-out hari ini"
        );
    }

    attendance.setJamKeluar(LocalDateTime.now());

    return attendanceRepository.save(attendance);
}
}