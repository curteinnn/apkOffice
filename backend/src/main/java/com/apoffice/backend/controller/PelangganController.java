package com.apoffice.backend.controller;

import com.apoffice.backend.dto.SavePelangganRequest;
import com.apoffice.backend.entity.Pelanggan;
import com.apoffice.backend.service.PelangganService;
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
@RequestMapping("/api/pelanggan")
public class PelangganController {
    private final PelangganService service;

    public PelangganController(PelangganService service) { this.service = service; }

    @GetMapping
    public List<Pelanggan> getAll() { return service.getAll(); }

    @PostMapping
    public Pelanggan create(@RequestBody SavePelangganRequest request) { return service.create(request); }

    @PutMapping("/{id}")
    public Pelanggan update(@PathVariable Long id, @RequestBody SavePelangganRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
