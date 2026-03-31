package org.example.service;

import org.example.dto.ExpenseRequest;
import org.example.dto.ExpenseResponse;
import org.example.entity.Category;
import org.example.entity.Expense;
import org.example.entity.User;
import org.example.repository.CategoryRepository;
import org.example.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public ExpenseResponse createExpense(User user, ExpenseRequest request) {
        Set<Category> categories = new HashSet<>();
        for (Long categoryId : request.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            categories.add(category);
        }

        Expense expense = Expense.builder()
                .user(user)
                .amount(request.getAmount())
                .description(request.getDescription())
                .date(request.getDate())
                .categories(categories)
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    public ExpenseResponse getExpenseById(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to access this expense");
        }

        return mapToResponse(expense);
    }

    public Page<ExpenseResponse> getAllExpenses(User user, Pageable pageable) {
        return expenseRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    public Page<ExpenseResponse> getExpensesByDateRange(User user, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return expenseRepository.findByUserAndDateBetween(user, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    public Page<ExpenseResponse> getExpensesByDateRangeAndCategory(User user, LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable) {
        return expenseRepository.findByUserAndDateRangeAndCategory(user, startDate, endDate, categoryId, pageable)
                .map(this::mapToResponse);
    }

    public ExpenseResponse updateExpense(Long expenseId, User user, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this expense");
        }

        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());

        Set<Category> categories = new HashSet<>();
        for (Long categoryId : request.getCategoryIds()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            categories.add(category);
        }
        expense.setCategories(categories);

        Expense updatedExpense = expenseRepository.save(expense);
        return mapToResponse(updatedExpense);
    }

    public void deleteExpense(Long expenseId, User user) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with id: " + expenseId));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .date(expense.getDate())
                .categories(expense.getCategories().stream()
                        .map(c -> new org.example.dto.CategoryResponse(c.getId(), c.getName(), c.getDescription()))
                        .collect(Collectors.toSet()))
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
