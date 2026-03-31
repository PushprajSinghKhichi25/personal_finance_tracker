package org.example.repository;

import org.example.entity.Expense;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByUser(User user, Pageable pageable);

    Page<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.date BETWEEN :startDate AND :endDate AND :categoryId MEMBER OF e.categories")
    Page<Expense> findByUserAndDateRangeAndCategory(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}