package com.apoffice.backend.service;

import com.apoffice.backend.dto.CreateVisitRequest;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.entity.Visit;
import com.apoffice.backend.repository.UserRepository;
import com.apoffice.backend.repository.VisitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final UserRepository userRepository;

    public VisitService(
            VisitRepository visitRepository,
            UserRepository userRepository
    ) {
        this.visitRepository = visitRepository;
        this.userRepository = userRepository;
    }

    public Visit createVisit(CreateVisitRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Employee tidak ditemukan"));

        if (user.getRole() != User.Role.EMPLOYEE) {
            throw new RuntimeException("Visit hanya bisa diberikan kepada employee");
        }

        Visit visit = new Visit();

        visit.setUser(user);
        visit.setJudul(request.getJudul());
        visit.setLokasi(request.getLokasi());
        visit.setDeskripsi(request.getDeskripsi());
        visit.setTanggal(request.getTanggal());
        visit.setStatus(Visit.Status.PENDING);

        return visitRepository.save(visit);
    }

    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }

    public List<Visit> getEmployeeVisits(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return visitRepository.findByUser(user);
    }
}