package com.apoffice.backend.repository;

import com.apoffice.backend.entity.Visit;
import com.apoffice.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    List<Visit> findByUser(User user);

}