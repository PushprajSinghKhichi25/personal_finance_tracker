package org.example.service;

import org.example.dto.CategoryBreakdownResponse;
import org.example.dto.MonthlyReportResponse;
import org.example.dto.TrendResponse;
import org.example.entity.*;
import org.example.repository.BudgetRepository;
import org.example.repository.ExpenseRepository;
import org.example.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final BudgetRepository budgetRepository;

    public ReportService(ExpenseRepository expenseRepository,
                         IncomeRepository incomeRepository,
                         BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
        this.budgetRepository = budgetRepository;
    }

    public MonthlyReportResponse getMonthlyReport(User user, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Calculate total income for the month
        List<Income> incomes = incomeRepository.findByUserAndDateBetween(user, startDate, endDate);
        BigDecimal totalIncome = incomes.stream()
                .map(Income::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total expenses for the month
        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate expenses by category
        Map<String, BigDecimal> byCategory = calculateCategoryBreakdown(expenses);

        // Calculate net
        BigDecimal net = totalIncome.subtract(totalExpenses);

        // Get budget status for the month
        Map<String, MonthlyReportResponse.BudgetStatusInfo> budgetStatus = getBudgetStatus(user, expenses, month);

        return MonthlyReportResponse.builder()
                .month(month)
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .net(net)
                .byCategory(byCategory)
                .budgetStatus(budgetStatus)
                .build();
    }

    public CategoryBreakdownResponse getCategoryBreakdown(User user, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
        Map<String, BigDecimal> categoryExpenses = calculateCategoryBreakdown(expenses);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CategoryBreakdownResponse.builder()
                .month(month)
                .categoryExpenses(categoryExpenses)
                .totalExpenses(totalExpenses)
                .build();
    }

    public TrendResponse getTrend(User user, int months) {
        List<TrendResponse.MonthlyTrendData> trends = new ArrayList<>();

        for (int i = months - 1; i >= 0; i--) {
            YearMonth targetMonth = YearMonth.now().minusMonths(i);
            LocalDate startDate = targetMonth.atDay(1);
            LocalDate endDate = targetMonth.atEndOfMonth();

            // Get income and expenses for this month
            List<Income> incomes = incomeRepository.findByUserAndDateBetween(user, startDate, endDate);
            BigDecimal totalIncome = incomes.stream()
                    .map(Income::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            List<Expense> expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);
            BigDecimal totalExpenses = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal net = totalIncome.subtract(totalExpenses);

            trends.add(TrendResponse.MonthlyTrendData.builder()
                    .month(targetMonth.toString())
                    .totalIncome(totalIncome)
                    .totalExpenses(totalExpenses)
                    .net(net)
                    .build());
        }

        return TrendResponse.builder()
                .trends(trends)
                .build();
    }

    private Map<String, BigDecimal> calculateCategoryBreakdown(List<Expense> expenses) {
        Map<String, BigDecimal> categoryBreakdown = new TreeMap<>();

        for (Expense expense : expenses) {
            for (Category category : expense.getCategories()) {
                categoryBreakdown.merge(category.getName(),
                        expense.getAmount(),
                        BigDecimal::add);
            }
        }

        return categoryBreakdown;
    }

    private Map<String, MonthlyReportResponse.BudgetStatusInfo> getBudgetStatus(User user, List<Expense> expenses, String month) {
        Map<String, MonthlyReportResponse.BudgetStatusInfo> budgetStatus = new HashMap<>();

        List<Budget> budgets = budgetRepository.findByUserAndCurrentMonth(user, month);

        for (Budget budget : budgets) {
            String categoryName = budget.getCategory().getName();

            // Calculate spent amount for this category
            BigDecimal spent = expenses.stream()
                    .filter(expense -> expense.getCategories().contains(budget.getCategory()))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);

            budgetStatus.put(categoryName, MonthlyReportResponse.BudgetStatusInfo.builder()
                    .limit(budget.getMonthlyLimit())
                    .spent(spent)
                    .remaining(remaining)
                    .build());
        }

        return budgetStatus;
    }
}