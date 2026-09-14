package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Tugas;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apoffice.backend.entity.User;
import java.util.List;

public interface TugasRepository extends JpaRepository<Tugas, Long> {
    List<Tugas> findByUser(User user);
}