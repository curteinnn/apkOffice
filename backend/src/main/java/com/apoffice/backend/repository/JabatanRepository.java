package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Jabatan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JabatanRepository extends JpaRepository<Jabatan, Long> {
    List<Jabatan> findAllByOrderByNamaAsc();
    List<Jabatan> findAllByStatusOrderByNamaAsc(Jabatan.Status status);
    Optional<Jabatan> findByNamaNormalized(String normalizedName);
}
