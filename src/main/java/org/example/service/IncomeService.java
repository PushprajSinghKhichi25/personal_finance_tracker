package org.example.service;

import org.example.dto.IncomeRequest;
import org.example.dto.IncomeResponse;
import org.example.entity.Income;
import org.example.entity.User;
import org.example.repository.IncomeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class IncomeService {
    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public IncomeResponse createIncome(User user, IncomeRequest request) {
        Income income = Income.builder()
                .user(user)
                .amount(request.getAmount())
                .description(request.getDescription())
                .source(request.getSource())
                .date(request.getDate())
                .build();

        Income savedIncome = incomeRepository.save(income);
        return mapToResponse(savedIncome);
    }

    public IncomeResponse getIncomeById(Long incomeId, User user) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + incomeId));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to access this income");
        }

        return mapToResponse(income);
    }

    public Page<IncomeResponse> getAllIncome(User user, Pageable pageable) {
        return incomeRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    public Page<IncomeResponse> getIncomeByDateRange(User user, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return incomeRepository.findByUserAndDateBetween(user, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    public IncomeResponse updateIncome(Long incomeId, User user, IncomeRequest request) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + incomeId));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this income");
        }

        income.setAmount(request.getAmount());
        income.setDescription(request.getDescription());
        income.setSource(request.getSource());
        income.setDate(request.getDate());

        Income updatedIncome = incomeRepository.save(income);
        return mapToResponse(updatedIncome);
    }

    public void deleteIncome(Long incomeId, User user) {
        Income income = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new IllegalArgumentException("Income not found with id: " + incomeId));

        if (!income.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this income");
        }

        incomeRepository.delete(income);
    }

    private IncomeResponse mapToResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .description(income.getDescription())
                .source(income.getSource())
                .date(income.getDate())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .build();
    }
}