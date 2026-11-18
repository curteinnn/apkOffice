package com.apoffice.backend.service;

import com.apoffice.backend.dto.JabatanRequest;
import com.apoffice.backend.dto.UpdateJabatanStatusRequest;
import com.apoffice.backend.entity.Jabatan;
import com.apoffice.backend.repository.JabatanRepository;
import com.apoffice.backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JabatanService {
    private final JabatanRepository jabatanRepository;
    private final UserRepository userRepository;

    public JabatanService(JabatanRepository jabatanRepository, UserRepository userRepository) {
        this.jabatanRepository = jabatanRepository;
        this.userRepository = userRepository;
    }

    public List<Jabatan> getAll(boolean activeOnly) {
        return activeOnly
                ? jabatanRepository.findAllByStatusOrderByNamaAsc(Jabatan.Status.ACTIVE)
                : jabatanRepository.findAllByOrderByNamaAsc();
    }

    public Jabatan getById(Long id) {
        return jabatanRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Jabatan tidak ditemukan"));
    }

    @Transactional
    public Jabatan create(JabatanRequest request) {
        String name = normalizeDisplayName(request == null ? null : request.getNama());
        ensureNameAvailable(name, null);
        LocalDateTime now = LocalDateTime.now();
        Jabatan jabatan = new Jabatan();
        jabatan.setNama(name);
        jabatan.setStatus(Jabatan.Status.ACTIVE);
        jabatan.setCreatedAt(now);
        jabatan.setUpdatedAt(now);
        return jabatanRepository.save(jabatan);
    }

    @Transactional
    public Jabatan update(Long id, JabatanRequest request) {
        Jabatan jabatan = getById(id);
        String name = normalizeDisplayName(request == null ? null : request.getNama());
        ensureNameAvailable(name, id);
        jabatan.setNama(name);
        jabatan.setUpdatedAt(LocalDateTime.now());
        return jabatanRepository.save(jabatan);
    }

    @Transactional
    public Jabatan updateStatus(Long id, UpdateJabatanStatusRequest request) {
        Jabatan jabatan = getById(id);
        if (request == null || request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status jabatan wajib diisi");
        }
        try {
            jabatan.setStatus(Jabatan.Status.valueOf(request.getStatus().trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status jabatan tidak valid");
        }
        jabatan.setUpdatedAt(LocalDateTime.now());
        return jabatanRepository.save(jabatan);
    }

    @Transactional
    public void delete(Long id) {
        Jabatan jabatan = getById(id);
        if (userRepository.existsByJabatanRef_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Jabatan masih digunakan oleh data karyawan; nonaktifkan jabatan sebagai gantinya");
        }
        jabatanRepository.delete(jabatan);
    }

    private String normalizeDisplayName(String value) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama jabatan wajib diisi");
        }
        String name = value.trim().replaceAll("\\s+", " ");
        if (name.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nama jabatan maksimal 100 karakter");
        }
        return name;
    }

    private void ensureNameAvailable(String name, Long currentId) {
        String key = name.trim().toLowerCase(Locale.ROOT);
        jabatanRepository.findByNamaNormalized(key).ifPresent(existing -> {
            if (!existing.getId().equals(currentId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Nama jabatan sudah terdaftar");
            }
        });
    }
}
