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
public class CategoryBreakdownResponse {
    private String month;
    private Map<String, BigDecimal> categoryExpenses;
    private BigDecimal totalExpenses;
}