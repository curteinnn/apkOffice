package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import com.apoffice.backend.entity.User;
import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndTanggal(User user, LocalDate tanggal);
}