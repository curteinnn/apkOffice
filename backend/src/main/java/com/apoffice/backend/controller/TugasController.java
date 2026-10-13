package com.apoffice.backend.controller;

import com.apoffice.backend.dto.CreateTugasRequest;
import com.apoffice.backend.dto.VerifyTugasRequest;
import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.service.TugasService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tugas")
public class TugasController {

    private final TugasService tugasService;

    public TugasController(TugasService tugasService) {
        this.tugasService = tugasService;
    }

    @GetMapping
    public List<Tugas> getAllTugas(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        String role = (String) httpRequest.getAttribute("role");

        return tugasService.getAllTugas(username, role);
    }

    @PostMapping
    public Tugas createTugas(
            @RequestBody CreateTugasRequest request,
            HttpServletRequest httpRequest
    ) {
        String role = (String) httpRequest.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
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
        String username = (String) httpRequest.getAttribute("username");
        String role = (String) httpRequest.getAttribute("role");

        if (!"EMPLOYEE".equals(role)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
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
        String role = (String) httpRequest.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "Hanya admin yang dapat memverifikasi tugas"
            );
        }

        return tugasService.verifyTugas(id, request);
    }
}