package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Pelanggan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PelangganRepository extends JpaRepository<Pelanggan, Long> {
}
