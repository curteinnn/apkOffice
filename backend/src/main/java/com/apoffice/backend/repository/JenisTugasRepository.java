package com.apoffice.backend.repository;

import com.apoffice.backend.entity.JenisTugas;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JenisTugasRepository extends JpaRepository<JenisTugas, String> {
    List<JenisTugas> findAllByOrderByIdAsc();
}
