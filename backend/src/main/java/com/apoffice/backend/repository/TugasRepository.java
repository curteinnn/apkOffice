package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Tugas;
import com.apoffice.backend.entity.Pelanggan;
import org.springframework.data.jpa.repository.JpaRepository;

import com.apoffice.backend.entity.User;
import java.util.List;

public interface TugasRepository extends JpaRepository<Tugas, Long> {
    boolean existsByPelanggan(Pelanggan pelanggan);
    List<Tugas> findByUser(User user);
}
