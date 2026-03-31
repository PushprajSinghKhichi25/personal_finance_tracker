package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetStatusResponse {
    private Long budgetId;
    private String categoryName;
    private BigDecimal limit;
    private BigDecimal spent;
    private BigDecimal remaining;
    private Double percentageUsed;
    private String status; // "UNDER", "ON_TRACK", "WARNING", "EXCEEDED"
}