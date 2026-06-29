package org.example.repository;

import org.example.entity.AIInsight;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AIInsightRepository extends JpaRepository<AIInsight, Long> {

    Optional<AIInsight> findByUserAndMonth(User user, String month);

    List<AIInsight> findByUserOrderByMonthDesc(User user);

    Page<AIInsight> findByUserOrderByMonthDesc(User user, Pageable pageable);

    List<AIInsight> findByMonthOrderByGeneratedAtDesc(String month);

    void deleteByUserAndMonth(User user, String month);
}
