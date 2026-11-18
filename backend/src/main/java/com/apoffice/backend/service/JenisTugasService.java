package com.apoffice.backend.service;

import com.apoffice.backend.dto.JenisTugasRequest;
import com.apoffice.backend.entity.JenisTugas;
import com.apoffice.backend.repository.JenisTugasRepository;
import com.apoffice.backend.repository.TugasRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JenisTugasService {
    private final JenisTugasRepository jenisTugasRepository;
    private final TugasRepository tugasRepository;

    public JenisTugasService(JenisTugasRepository jenisTugasRepository, TugasRepository tugasRepository) {
        this.jenisTugasRepository = jenisTugasRepository;
        this.tugasRepository = tugasRepository;
    }

    public List<JenisTugas> getAll() {
        return jenisTugasRepository.findAllByOrderByIdAsc();
    }

    public JenisTugas getById(String id) {
        return jenisTugasRepository.findById(normalizeId(id)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Jenis tugas tidak ditemukan"));
    }

    @Transactional
    public JenisTugas create(JenisTugasRequest request) {
        if (request == null) throw badRequest("Data jenis tugas wajib diisi");
        String id = normalizeId(request.id());
        if (jenisTugasRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ID jenis tugas sudah digunakan");
        }
        String nama = normalizeName(request.nama());
        ensureNameAvailable(nama, null);
        JenisTugas jenis = new JenisTugas();
        jenis.setId(id);
        jenis.setNama(nama);
        return jenisTugasRepository.save(jenis);
    }

    @Transactional
    public JenisTugas update(String id, JenisTugasRequest request) {
        JenisTugas jenis = getById(id);
        if (request == null) throw badRequest("Data jenis tugas wajib diisi");
        String nama = normalizeName(request.nama());
        ensureNameAvailable(nama, jenis.getId());
        jenis.setNama(nama);
        return jenisTugasRepository.save(jenis);
    }

    @Transactional
    public void delete(String id) {
        JenisTugas jenis = getById(id);
        if (tugasRepository.existsByJenisTugas_Id(jenis.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Jenis tugas masih digunakan oleh tugas dan tidak dapat dihapus");
        }
        jenisTugasRepository.delete(jenis);
    }

    private String normalizeId(String value) {
        if (value == null || value.isBlank()) throw badRequest("ID jenis tugas wajib diisi");
        String id = value.trim();
        if (id.length() > 20) throw badRequest("ID jenis tugas maksimal 20 karakter");
        return id;
    }

    private String normalizeName(String value) {
        if (value == null || value.isBlank()) throw badRequest("Nama jenis tugas wajib diisi");
        String nama = value.trim().replaceAll("\\s+", " ");
        if (nama.length() > 60) throw badRequest("Nama jenis tugas maksimal 60 karakter");
        return nama;
    }

    private void ensureNameAvailable(String nama, String currentId) {
        String normalized = nama.toLowerCase(Locale.ROOT);
        boolean exists = jenisTugasRepository.findAll().stream()
                .anyMatch(item -> !item.getId().equals(currentId)
                        && item.getNama().trim().replaceAll("\\s+", " ")
                                .toLowerCase(Locale.ROOT).equals(normalized));
        if (exists) throw new ResponseStatusException(HttpStatus.CONFLICT, "Nama jenis tugas sudah terdaftar");
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
