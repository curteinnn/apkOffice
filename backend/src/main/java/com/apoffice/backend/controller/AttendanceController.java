package com.apoffice.backend.controller;

import com.apoffice.backend.entity.Attendance;
import com.apoffice.backend.service.AttendanceService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<Attendance> getAllAttendance(
            HttpServletRequest request
    ) {
        String role = (String) request.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya admin yang dapat melihat semua absensi"
            );
        }

        return attendanceService.getAllAttendance();
    }

    @GetMapping("/my")
    public List<Attendance> getMyAttendance(
            HttpServletRequest request
    ) {
        String username = (String) request.getAttribute("username");

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token login tidak valid"
            );
        }

        return attendanceService.getMyAttendance(username);
    }

    @PostMapping("/check-in")
    public Attendance checkIn(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String role = (String) request.getAttribute("role");

        if (!"EMPLOYEE".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya karyawan yang dapat melakukan check-in"
            );
        }

        return attendanceService.checkIn(username);
    }

    @PostMapping("/check-out")
    public Attendance checkOut(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String role = (String) request.getAttribute("role");

        if (!"EMPLOYEE".equals(role)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Hanya karyawan yang dapat melakukan check-out"
            );
        }

        return attendanceService.checkOut(username);
    }
}