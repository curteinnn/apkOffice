package com.apoffice.backend.controller;

import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.service.TugasService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apoffice.backend.dto.CreateTugasRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/tugas")
public class TugasController {

    private final TugasService tugasService;

    public TugasController(TugasService tugasService) {
        this.tugasService = tugasService;
    }

    @GetMapping
    public List<Tugas> getAllTugas() {
        return tugasService.getAllTugas();
    }
    
    @PostMapping
    public Tugas createTugas(
        @RequestBody CreateTugasRequest request,
        HttpServletRequest httpRequest
    ){
        String username = (String) httpRequest.getAttribute("username");
        return tugasService.createTugas(request, username);
    }
}