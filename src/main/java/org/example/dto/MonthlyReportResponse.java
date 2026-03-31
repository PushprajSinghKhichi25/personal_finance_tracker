package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyReportResponse {
    private String month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal net;
    private Map<String, BigDecimal> byCategory;
    private Map<String, BudgetStatusInfo> budgetStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BudgetStatusInfo {
        private BigDecimal limit;
        private BigDecimal spent;
        private BigDecimal remaining;
    }
}