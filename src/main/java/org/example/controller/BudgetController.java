package org.example.controller;

import org.example.dto.BudgetRequest;
import org.example.dto.BudgetResponse;
import org.example.dto.BudgetStatusResponse;
import org.example.entity.User;
import org.example.service.BudgetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        BudgetResponse response = budgetService.createBudget(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        BudgetResponse response = budgetService.getBudgetById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<BudgetResponse>> getAllBudgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String month,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);

        Page<BudgetResponse> response;
        if (month != null && !month.isBlank()) {
            response = budgetService.getBudgetsByMonth(user, month, pageable);
        } else {
            response = budgetService.getCurrentMonthBudgets(user, pageable);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        BudgetResponse response = budgetService.updateBudget(id, user, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        budgetService.deleteBudget(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<BudgetStatusResponse> getBudgetStatus(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        BudgetStatusResponse response = budgetService.getBudgetStatus(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/all")
    public ResponseEntity<List<BudgetStatusResponse>> getAllBudgetsStatus(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        List<BudgetStatusResponse> response = budgetService.getAllBudgetsStatus(user);
        return ResponseEntity.ok(response);
    }
}