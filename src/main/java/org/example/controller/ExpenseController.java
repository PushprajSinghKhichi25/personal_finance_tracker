package org.example.controller;

import org.example.dto.ExpenseRequest;
import org.example.dto.ExpenseResponse;
import org.example.entity.User;
import org.example.service.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        ExpenseResponse response = expenseService.createExpense(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        ExpenseResponse response = expenseService.getExpenseById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getAllExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        Page<ExpenseResponse> response;
        if (startDate != null && endDate != null && categoryId != null) {
            response = expenseService.getExpensesByDateRangeAndCategory(user, startDate, endDate, categoryId, pageable);
        } else if (startDate != null && endDate != null) {
            response = expenseService.getExpensesByDateRange(user, startDate, endDate, pageable);
        } else {
            response = expenseService.getAllExpenses(user, pageable);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        ExpenseResponse response = expenseService.updateExpense(id, user, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        expenseService.deleteExpense(id, user);
        return ResponseEntity.noContent().build();
    }
}