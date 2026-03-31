package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Set<CategoryResponse> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}