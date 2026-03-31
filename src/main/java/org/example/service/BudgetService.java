package org.example.service;

import org.example.dto.BudgetRequest;
import org.example.dto.BudgetResponse;
import org.example.dto.BudgetStatusResponse;
import org.example.dto.CategoryResponse;
import org.example.entity.Budget;
import org.example.entity.Category;
import org.example.entity.Expense;
import org.example.entity.User;
import org.example.repository.BudgetRepository;
import org.example.repository.CategoryRepository;
import org.example.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    public BudgetResponse createBudget(User user, BudgetRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        String currentMonth = request.getCurrentMonth() != null ? request.getCurrentMonth() : YearMonth.now().toString();

        // Check if budget already exists for this category in this month
        if (budgetRepository.findByUserAndCategoryIdAndCurrentMonth(user, request.getCategoryId(), currentMonth).isPresent()) {
            throw new IllegalArgumentException("Budget already exists for category: " + category.getName() + " in month: " + currentMonth);
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .monthlyLimit(request.getMonthlyLimit())
                .currentMonth(currentMonth)
                .build();

        Budget savedBudget = budgetRepository.save(budget);
        return mapToResponse(savedBudget);
    }

    public BudgetResponse getBudgetById(Long budgetId, User user) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to access this budget");
        }

        return mapToResponse(budget);
    }

    public Page<BudgetResponse> getAllBudgets(User user, Pageable pageable) {
        return budgetRepository.findByUser(user, pageable)
                .map(this::mapToResponse);
    }

    public Page<BudgetResponse> getBudgetsByMonth(User user, String month, Pageable pageable) {
        return budgetRepository.findByUserAndCurrentMonth(user, month, pageable)
                .map(this::mapToResponse);
    }

    public Page<BudgetResponse> getCurrentMonthBudgets(User user, Pageable pageable) {
        String currentMonth = YearMonth.now().toString();
        return getBudgetsByMonth(user, currentMonth, pageable);
    }

    public BudgetResponse updateBudget(Long budgetId, User user, BudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to update this budget");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + request.getCategoryId()));

        budget.setCategory(category);
        budget.setMonthlyLimit(request.getMonthlyLimit());

        Budget updatedBudget = budgetRepository.save(budget);
        return mapToResponse(updatedBudget);
    }

    public void deleteBudget(Long budgetId, User user) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to delete this budget");
        }

        budgetRepository.delete(budget);
    }

    public BudgetStatusResponse getBudgetStatus(Long budgetId, User user) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));

        if (!budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You don't have permission to access this budget");
        }

        return calculateBudgetStatus(budget, user);
    }

    public List<BudgetStatusResponse> getAllBudgetsStatus(User user) {
        String currentMonth = YearMonth.now().toString();
        List<Budget> budgets = budgetRepository.findByUserAndCurrentMonth(user, currentMonth);
        return budgets.stream()
                .map(budget -> calculateBudgetStatus(budget, user))
                .toList();
    }

    private BudgetStatusResponse calculateBudgetStatus(Budget budget, User user) {
        // Get all expenses for this user in this month with this category
        BigDecimal spent = BigDecimal.ZERO;

        // Parse the month from the budget
        String[] monthParts = budget.getCurrentMonth().split("-");
        int year = Integer.parseInt(monthParts[0]);
        int month = Integer.parseInt(monthParts[1]);

        // Get all expenses for the category in the month
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(
                user,
                java.time.LocalDate.of(year, month, 1),
                java.time.LocalDate.of(year, month,
                        java.time.YearMonth.of(year, month).lengthOfMonth())
        );

        for (Expense expense : expenses) {
            if (expense.getCategories().contains(budget.getCategory())) {
                spent = spent.add(expense.getAmount());
            }
        }

        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);
        double percentageUsed = spent.doubleValue() / budget.getMonthlyLimit().doubleValue() * 100;

        String status;
        if (spent.compareTo(budget.getMonthlyLimit()) > 0) {
            status = "EXCEEDED";
        } else if (percentageUsed >= 80) {
            status = "WARNING";
        } else if (percentageUsed >= 50) {
            status = "ON_TRACK";
        } else {
            status = "UNDER";
        }

        return BudgetStatusResponse.builder()
                .budgetId(budget.getId())
                .categoryName(budget.getCategory().getName())
                .limit(budget.getMonthlyLimit())
                .spent(spent)
                .remaining(remaining)
                .percentageUsed(percentageUsed)
                .status(status)
                .build();
    }

    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .category(new CategoryResponse(budget.getCategory().getId(), budget.getCategory().getName(), budget.getCategory().getDescription()))
                .monthlyLimit(budget.getMonthlyLimit())
                .currentMonth(budget.getCurrentMonth())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}