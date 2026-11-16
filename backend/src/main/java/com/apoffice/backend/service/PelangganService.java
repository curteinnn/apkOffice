package com.apoffice.backend.service;

import com.apoffice.backend.dto.SavePelangganRequest;
import com.apoffice.backend.entity.Pelanggan;
import com.apoffice.backend.repository.PelangganRepository;
import com.apoffice.backend.repository.TugasRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PelangganService {
    private final PelangganRepository pelangganRepository;
    private final TugasRepository tugasRepository;

    public PelangganService(PelangganRepository pelangganRepository, TugasRepository tugasRepository) {
        this.pelangganRepository = pelangganRepository;
        this.tugasRepository = tugasRepository;
    }

    public List<Pelanggan> getAll() { return pelangganRepository.findAll(); }

    @Transactional
    public Pelanggan create(SavePelangganRequest request) {
        Pelanggan pelanggan = new Pelanggan();
        apply(pelanggan, request);
        return pelangganRepository.save(pelanggan);
    }

    @Transactional
    public Pelanggan update(Long id, SavePelangganRequest request) {
        Pelanggan pelanggan = pelangganRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pelanggan tidak ditemukan"));
        apply(pelanggan, request);
        return pelangganRepository.save(pelanggan);
    }

    @Transactional
    public void delete(Long id) {
        Pelanggan pelanggan = pelangganRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pelanggan tidak ditemukan"));
        if (tugasRepository.existsByPelanggan(pelanggan)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pelanggan sudah digunakan pada tugas dan tidak dapat dihapus");
        }
        pelangganRepository.delete(pelanggan);
    }

    private void apply(Pelanggan pelanggan, SavePelangganRequest request) {
        if (request.getNama() == null || request.getNama().isBlank()
                || request.getNama().trim().length() > 120
                || request.getAlamat() == null || request.getAlamat().isBlank()
                || request.getAlamat().trim().length() > 255
                || exceeds(request.getNamaKontak(), 100)
                || exceeds(request.getTelepon(), 30)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nama (maks. 120 karakter) dan alamat (maks. 255 karakter) wajib diisi; kontak dan telepon juga harus sesuai batas panjang");
        }
        pelanggan.setNama(request.getNama().trim());
        pelanggan.setNamaKontak(trimToNull(request.getNamaKontak()));
        pelanggan.setTelepon(trimToNull(request.getTelepon()));
        pelanggan.setAlamat(request.getAlamat().trim());
        pelanggan.setLinkGmaps(trimToNull(request.getLinkGmaps()));
    }

    private boolean exceeds(String value, int maxLength) {
        return value != null && value.trim().length() > maxLength;
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
