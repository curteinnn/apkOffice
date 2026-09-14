package com.apoffice.backend.service;

import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.repository.TugasRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import com.apoffice.backend.dto.CreateTugasRequest;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TugasService {

    private final TugasRepository tugasRepository;
    private final UserRepository userRepository;

    public TugasService(
        TugasRepository tugasRepository,
        UserRepository userRepository  
    ){
    this.tugasRepository = tugasRepository;
    this.userRepository = userRepository;
    }

    public List<Tugas> getAllTugas() {
        return tugasRepository.findAll();
    }
    
    public Tugas createTugas(CreateTugasRequest request, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User tidak ditemukan"
                        )
                );

                Tugas tugas = new Tugas();

                tugas.setUser(user);
                tugas.setJudul(request.getJudul());
                tugas.setDeskripsi(request.getDeskripsi());
                tugas.setFotoBukti(request.getFotoBukti());

                tugas.setStatus("PENDING");
                tugas.setNominal(null);
                tugas.setTanggal(LocalDate.now());
                tugas.setCreatedAt(LocalDateTime.now());
                tugas.setUpdatedAt(LocalDateTime.now());

    return tugasRepository.save(tugas);
}
}