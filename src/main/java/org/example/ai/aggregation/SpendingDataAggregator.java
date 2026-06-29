package org.example.ai.aggregation;

import org.example.dto.SpendingDataDTO;
import org.example.entity.Budget;
import org.example.entity.Expense;
import org.example.entity.User;
import org.example.repository.BudgetRepository;
import org.example.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SpendingDataAggregator {

    private static final Logger logger = LoggerFactory.getLogger(SpendingDataAggregator.class);

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    public SpendingDataAggregator(ExpenseRepository expenseRepository,
                                  BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }


    public SpendingDataDTO getMonthlySpendingData(User user, YearMonth month) {
        logger.debug("Aggregating spending data for user {} in month {}", user.getId(), month);

        SpendingDataDTO data = new SpendingDataDTO();
        data.setUserId(user.getId());
        data.setMonth(month);

        // Get expenses for current, previous months
        LocalDate monthStart = month.atDay(1);
        LocalDate monthEnd = month.atEndOfMonth();
        LocalDate previousMonthStart = month.minusMonths(1).atDay(1);
        LocalDate previousMonthEnd = month.minusMonths(1).atEndOfMonth();

        List<Expense> currentMonthExpenses = expenseRepository.findByUserAndDateBetween(
                user, monthStart, monthEnd
        );
        List<Expense> previousMonthExpenses = expenseRepository.findByUserAndDateBetween(
                user, previousMonthStart, previousMonthEnd
        );

        // Calculate totals
        BigDecimal currentMonthTotal = currentMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal previousMonthTotal = previousMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        data.setCurrentMonthTotal(currentMonthTotal);
        data.setPreviousMonthTotal(previousMonthTotal);

        // Get 3-month average
        BigDecimal threeMonthAverage = getThreeMonthAverage(user, month);
        data.setThreeMonthAverage(threeMonthAverage);

        // Category breakdown
        Map<String, SpendingDataDTO.CategorySpendingDTO> categoryBreakdown =
                getCategoryBreakdown(user, month, currentMonthExpenses, currentMonthTotal);
        data.setCategoryBreakdown(categoryBreakdown);

        // Trends for last 3 months
        List<SpendingDataDTO.MonthlyTrendDTO> trends = getMonthlyTrends(user, month);
        data.setTrends(trends);

        // Budget status for current month
        List<SpendingDataDTO.BudgetStatusDTO> budgetStatus = getBudgetStatus(user, month);
        data.setBudgetStatus(budgetStatus);

        logger.debug("Spending data aggregated: currentTotal={}, previousTotal={}, categories={}",
                currentMonthTotal, previousMonthTotal, categoryBreakdown.size());

        return data;
    }


    private BigDecimal getThreeMonthAverage(User user, YearMonth month) {
        List<BigDecimal> monthlyTotals = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            YearMonth targetMonth = month.minusMonths(i);
            LocalDate monthStart = targetMonth.atDay(1);
            LocalDate monthEnd = targetMonth.atEndOfMonth();

            BigDecimal monthlyTotal = expenseRepository.findByUserAndDateBetween(
                            user, monthStart, monthEnd
                    ).stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyTotals.add(monthlyTotal);
        }

        return TrendCalculator.calculateAverage(monthlyTotals);
    }


    private Map<String, SpendingDataDTO.CategorySpendingDTO> getCategoryBreakdown(
            User user, YearMonth month, List<Expense> currentMonthExpenses, BigDecimal currentMonthTotal) {

        Map<String, SpendingDataDTO.CategorySpendingDTO> breakdown = new HashMap<>();

        // Group expenses by category
        Map<String, List<Expense>> categorizedExpenses = new HashMap<>();
        for (Expense expense : currentMonthExpenses) {
            for (var category : expense.getCategories()) {
                String categoryName = category.getName();
                categorizedExpenses.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(expense);
            }
        }

        // Calculate totals and percentages for each category
        for (Map.Entry<String, List<Expense>> entry : categorizedExpenses.entrySet()) {
            String categoryName = entry.getKey();
            List<Expense> expenses = entry.getValue();

            BigDecimal categoryTotal = expenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double percentOfTotal = currentMonthTotal.compareTo(BigDecimal.ZERO) > 0
                    ? (categoryTotal.doubleValue() / currentMonthTotal.doubleValue()) * 100
                    : 0;

            // Get previous month amount for comparison
            YearMonth previousMonth = month.minusMonths(1);
            LocalDate prevMonthStart = previousMonth.atDay(1);
            LocalDate prevMonthEnd = previousMonth.atEndOfMonth();

            List<Expense> previousMonthCategoryExpenses = expenseRepository.findByUserAndDateBetween(
                            user, prevMonthStart, prevMonthEnd
                    ).stream()
                    .filter(e -> e.getCategories().stream().anyMatch(c -> c.getName().equals(categoryName)))
                    .toList();

            BigDecimal previousCategoryTotal = previousMonthCategoryExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            double percentChange = TrendCalculator.calculatePercentChange(previousCategoryTotal, categoryTotal);

            SpendingDataDTO.CategorySpendingDTO categoryData = new SpendingDataDTO.CategorySpendingDTO(
                    categoryName, categoryTotal, percentOfTotal
            );
            categoryData.setPreviousMonthAmount(previousCategoryTotal);
            categoryData.setPercentChange(percentChange);

            breakdown.put(categoryName, categoryData);
        }

        return breakdown;
    }


    private List<SpendingDataDTO.MonthlyTrendDTO> getMonthlyTrends(User user, YearMonth month) {
        List<SpendingDataDTO.MonthlyTrendDTO> trends = new ArrayList<>();

        for (int i = 2; i >= 0; i--) {
            YearMonth targetMonth = month.minusMonths(i);
            LocalDate monthStart = targetMonth.atDay(1);
            LocalDate monthEnd = targetMonth.atEndOfMonth();

            BigDecimal monthlyTotal = expenseRepository.findByUserAndDateBetween(
                            user, monthStart, monthEnd
                    ).stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            trends.add(new SpendingDataDTO.MonthlyTrendDTO(targetMonth, monthlyTotal));
        }

        return trends;
    }


    private List<SpendingDataDTO.BudgetStatusDTO> getBudgetStatus(User user, YearMonth month) {
        String monthString = month.toString();
        List<Budget> budgets = budgetRepository.findByUserAndCurrentMonth(user, monthString);

        List<SpendingDataDTO.BudgetStatusDTO> budgetStatuses = new ArrayList<>();

        for (Budget budget : budgets) {
            String categoryName = budget.getCategory().getName();

            // Get spent amount for this category this month
            LocalDate monthStart = month.atDay(1);
            LocalDate monthEnd = month.atEndOfMonth();

            List<Expense> categoryExpenses = expenseRepository.findByUserAndDateBetween(
                            user, monthStart, monthEnd
                    ).stream()
                    .filter(e -> e.getCategories().stream()
                            .anyMatch(c -> c.getName().equals(categoryName)))
                    .toList();

            BigDecimal spent = categoryExpenses.stream()
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            SpendingDataDTO.BudgetStatusDTO status = new SpendingDataDTO.BudgetStatusDTO(
                    categoryName, budget.getMonthlyLimit(), spent
            );

            budgetStatuses.add(status);
        }

        return budgetStatuses;
    }

    public BigDecimal getTotalSpending(User user, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserAndDateBetween(user, startDate, endDate).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getCategorySpending(User user, String categoryName, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserAndDateBetween(user, startDate, endDate).stream()
                .filter(e -> e.getCategories().stream().anyMatch(c -> c.getName().equals(categoryName)))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}