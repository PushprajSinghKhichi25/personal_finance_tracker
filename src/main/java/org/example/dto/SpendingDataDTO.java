package org.example.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;


public class SpendingDataDTO {

    private Long userId;
    private YearMonth month;
    private BigDecimal currentMonthTotal;
    private BigDecimal previousMonthTotal;
    private BigDecimal threeMonthAverage;
    private Map<String, CategorySpendingDTO> categoryBreakdown;
    private List<MonthlyTrendDTO> trends;
    private List<BudgetStatusDTO> budgetStatus;

    public SpendingDataDTO() {
    }

    public SpendingDataDTO(Long userId, YearMonth month) {
        this.userId = userId;
        this.month = month;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public BigDecimal getCurrentMonthTotal() {
        return currentMonthTotal;
    }

    public void setCurrentMonthTotal(BigDecimal currentMonthTotal) {
        this.currentMonthTotal = currentMonthTotal;
    }

    public BigDecimal getPreviousMonthTotal() {
        return previousMonthTotal;
    }

    public void setPreviousMonthTotal(BigDecimal previousMonthTotal) {
        this.previousMonthTotal = previousMonthTotal;
    }

    public BigDecimal getThreeMonthAverage() {
        return threeMonthAverage;
    }

    public void setThreeMonthAverage(BigDecimal threeMonthAverage) {
        this.threeMonthAverage = threeMonthAverage;
    }

    public Map<String, CategorySpendingDTO> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(Map<String, CategorySpendingDTO> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<MonthlyTrendDTO> getTrends() {
        return trends;
    }

    public void setTrends(List<MonthlyTrendDTO> trends) {
        this.trends = trends;
    }

    public List<BudgetStatusDTO> getBudgetStatus() {
        return budgetStatus;
    }

    public void setBudgetStatus(List<BudgetStatusDTO> budgetStatus) {
        this.budgetStatus = budgetStatus;
    }


    public static class CategorySpendingDTO {
        private String categoryName;
        private BigDecimal amount;
        private double percentOfTotal;
        private BigDecimal previousMonthAmount;
        private double percentChange;

        public CategorySpendingDTO() {
        }

        public CategorySpendingDTO(String categoryName, BigDecimal amount, double percentOfTotal) {
            this.categoryName = categoryName;
            this.amount = amount;
            this.percentOfTotal = percentOfTotal;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public double getPercentOfTotal() {
            return percentOfTotal;
        }

        public void setPercentOfTotal(double percentOfTotal) {
            this.percentOfTotal = percentOfTotal;
        }

        public BigDecimal getPreviousMonthAmount() {
            return previousMonthAmount;
        }

        public void setPreviousMonthAmount(BigDecimal previousMonthAmount) {
            this.previousMonthAmount = previousMonthAmount;
        }

        public double getPercentChange() {
            return percentChange;
        }

        public void setPercentChange(double percentChange) {
            this.percentChange = percentChange;
        }
    }


    public static class MonthlyTrendDTO {
        private YearMonth month;
        private BigDecimal total;

        public MonthlyTrendDTO() {
        }

        public MonthlyTrendDTO(YearMonth month, BigDecimal total) {
            this.month = month;
            this.total = total;
        }

        public YearMonth getMonth() {
            return month;
        }

        public void setMonth(YearMonth month) {
            this.month = month;
        }

        public BigDecimal getTotal() {
            return total;
        }

        public void setTotal(BigDecimal total) {
            this.total = total;
        }
    }


    public static class BudgetStatusDTO {
        private String categoryName;
        private BigDecimal allocated;
        private BigDecimal spent;
        private BigDecimal remaining;
        private double percentageUsed;
        private boolean isOverBudget;

        public BudgetStatusDTO() {
        }

        public BudgetStatusDTO(String categoryName, BigDecimal allocated, BigDecimal spent) {
            this.categoryName = categoryName;
            this.allocated = allocated;
            this.spent = spent;
            this.remaining = allocated.subtract(spent);
            this.percentageUsed = (spent.doubleValue() / allocated.doubleValue()) * 100;
            this.isOverBudget = spent.compareTo(allocated) > 0;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public BigDecimal getAllocated() {
            return allocated;
        }

        public void setAllocated(BigDecimal allocated) {
            this.allocated = allocated;
        }

        public BigDecimal getSpent() {
            return spent;
        }

        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }

        public BigDecimal getRemaining() {
            return remaining;
        }

        public void setRemaining(BigDecimal remaining) {
            this.remaining = remaining;
        }

        public double getPercentageUsed() {
            return percentageUsed;
        }

        public void setPercentageUsed(double percentageUsed) {
            this.percentageUsed = percentageUsed;
        }

        public boolean isOverBudget() {
            return isOverBudget;
        }

        public void setOverBudget(boolean overBudget) {
            isOverBudget = overBudget;
        }
    }
}