package com.apoffice.backend.controller;

import com.apoffice.backend.dto.JabatanRequest;
import com.apoffice.backend.dto.UpdateJabatanStatusRequest;
import com.apoffice.backend.entity.Jabatan;
import com.apoffice.backend.service.JabatanService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jabatan")
public class JabatanController {
    private final JabatanService jabatanService;

    public JabatanController(JabatanService jabatanService) {
        this.jabatanService = jabatanService;
    }

    @GetMapping
    public List<Jabatan> getAll(@RequestParam(defaultValue = "false") boolean aktif) {
        return jabatanService.getAll(aktif);
    }

    @GetMapping("/{id}")
    public Jabatan getById(@PathVariable Long id) {
        return jabatanService.getById(id);
    }

    @PostMapping
    public Jabatan create(@RequestBody JabatanRequest request) {
        return jabatanService.create(request);
    }

    @PutMapping("/{id}")
    public Jabatan update(@PathVariable Long id, @RequestBody JabatanRequest request) {
        return jabatanService.update(id, request);
    }

    @PutMapping("/{id}/status")
    public Jabatan updateStatus(@PathVariable Long id, @RequestBody UpdateJabatanStatusRequest request) {
        return jabatanService.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        jabatanService.delete(id);
    }
}
