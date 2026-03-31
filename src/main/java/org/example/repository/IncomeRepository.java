package org.example.repository;

import org.example.entity.Income;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {
    Page<Income> findByUser(User user, Pageable pageable);

    Page<Income> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<Income> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}