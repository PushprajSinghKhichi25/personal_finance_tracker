package org.example.repository;

import org.example.entity.Budget;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Page<Budget> findByUser(User user, Pageable pageable);

    Page<Budget> findByUserAndCurrentMonth(User user, String currentMonth, Pageable pageable);

    List<Budget> findByUserAndCurrentMonth(User user, String currentMonth);

    Optional<Budget> findByUserAndCategoryIdAndCurrentMonth(User user, Long categoryId, String currentMonth);
}