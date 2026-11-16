package com.apoffice.backend.controller;

import com.apoffice.backend.entity.JenisTugas;
import com.apoffice.backend.repository.JenisTugasRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jenis-tugas")
public class JenisTugasController {

    private final JenisTugasRepository repository;

    public JenisTugasController(JenisTugasRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<JenisTugas> getAll() {
        return repository.findAllByOrderByIdAsc();
    }
}
