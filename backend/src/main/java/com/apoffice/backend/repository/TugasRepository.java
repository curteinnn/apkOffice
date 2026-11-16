package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Tugas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.apoffice.backend.entity.User;
import java.util.List;

public interface TugasRepository extends JpaRepository<Tugas, String> {
    @Query("select distinct t from Tugas t join t.penugasanKaryawan p where p.karyawan = :user")
    List<Tugas> findAssignedTo(@Param("user") User user);
}
