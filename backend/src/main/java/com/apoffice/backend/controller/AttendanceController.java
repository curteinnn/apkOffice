package com.apoffice.backend.controller;

import com.apoffice.backend.entity.Attendance;
import com.apoffice.backend.service.AttendanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
    
    @PostMapping("/check-in")
    public Attendance checkIn(HttpServletRequest request) {

    String username = (String) request.getAttribute("username");

    return attendanceService.checkIn(username);
    }
    
    @PostMapping("/check-out")
    public Attendance checkOut(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");

        return attendanceService.checkOut(username);
    }
}