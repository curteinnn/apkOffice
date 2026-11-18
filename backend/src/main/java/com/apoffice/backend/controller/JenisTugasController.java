package com.apoffice.backend.controller;

import com.apoffice.backend.dto.JenisTugasRequest;
import com.apoffice.backend.entity.JenisTugas;
import com.apoffice.backend.service.JenisTugasService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jenis-tugas")
public class JenisTugasController {
    private final JenisTugasService service;

    public JenisTugasController(JenisTugasService service) {
        this.service = service;
    }

    @GetMapping
    public List<JenisTugas> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public JenisTugas getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public JenisTugas create(@RequestBody JenisTugasRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public JenisTugas update(@PathVariable String id, @RequestBody JenisTugasRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
