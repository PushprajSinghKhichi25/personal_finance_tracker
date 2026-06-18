package org.example.controller;

import org.example.dto.IncomeRequest;
import org.example.dto.IncomeResponse;
import org.example.entity.User;
import org.example.service.IncomeService;
import org.example.util.SecurityUtil;
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
@RequestMapping("/api/income")
public class IncomeController {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication
    ) {
        //User user = (User) authentication.getPrincipal();
        User user = SecurityUtil.extractUser(authentication);
        IncomeResponse response = incomeService.createIncome(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getIncomeById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = SecurityUtil.extractUser(authentication);
        IncomeResponse response = incomeService.getIncomeById(id, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<IncomeResponse>> getAllIncome(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Authentication authentication
    ) {
        User user = SecurityUtil.extractUser(authentication);
        Pageable pageable = PageRequest.of(page, size);

        Page<IncomeResponse> response;
        if (startDate != null && endDate != null) {
            response = incomeService.getIncomeByDateRange(user, startDate, endDate, pageable);
        } else {
            response = incomeService.getAllIncome(user, pageable);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication
    ) {
        User user = SecurityUtil.extractUser(authentication);

        IncomeResponse response = incomeService.updateIncome(id, user, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = SecurityUtil.extractUser(authentication);

        incomeService.deleteIncome(id, user);
        return ResponseEntity.noContent().build();
    }
}