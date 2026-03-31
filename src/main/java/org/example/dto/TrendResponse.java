package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendResponse {
    private List<MonthlyTrendData> trends;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrendData {
        private String month;
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal net;
    }
}