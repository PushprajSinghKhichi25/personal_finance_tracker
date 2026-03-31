package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private String source;
    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}