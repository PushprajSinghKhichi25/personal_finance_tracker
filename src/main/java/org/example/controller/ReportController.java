package org.example.controller;

import org.example.dto.CategoryBreakdownResponse;
import org.example.dto.MonthlyReportResponse;
import org.example.dto.TrendResponse;
import org.example.entity.User;
import org.example.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @RequestParam String month,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        MonthlyReportResponse report = reportService.getMonthlyReport(user, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<CategoryBreakdownResponse> getCategoryBreakdown(
            @RequestParam String month,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CategoryBreakdownResponse breakdown = reportService.getCategoryBreakdown(user, month);
        return ResponseEntity.ok(breakdown);
    }

    @GetMapping("/trend")
    public ResponseEntity<TrendResponse> getTrend(
            @RequestParam(defaultValue = "3") int months,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        TrendResponse trend = reportService.getTrend(user, months);
        return ResponseEntity.ok(trend);
    }
}