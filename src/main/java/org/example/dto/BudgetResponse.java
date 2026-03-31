package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {
    private Long id;
    private CategoryResponse category;
    private BigDecimal monthlyLimit;
    private String currentMonth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}