package org.example.controller;

import org.example.ai.aggregation.SpendingDataAggregator;
import org.example.dto.SpendingDataDTO;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/ai/data")
public class DataAggregationController {

    private final SpendingDataAggregator spendingDataAggregator;
    private final UserRepository userRepository;

    public DataAggregationController(SpendingDataAggregator spendingDataAggregator,
                                     UserRepository userRepository) {
        this.spendingDataAggregator = spendingDataAggregator;
        this.userRepository = userRepository;
    }


    @GetMapping("/spending/{month}")
    public ResponseEntity<Map<String, Object>> getSpendingData(
            @PathVariable String month,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            YearMonth yearMonth = YearMonth.parse(month);
            SpendingDataDTO data = spendingDataAggregator.getMonthlySpendingData(user, yearMonth);

            Map<String, Object> response = new HashMap<>();
            response.put("month", month);
            response.put("userId", user.getId());
            response.put("data", data);
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/spending/current")
    public ResponseEntity<Map<String, Object>> getCurrentMonthSpending(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            YearMonth currentMonth = YearMonth.now();
            SpendingDataDTO data = spendingDataAggregator.getMonthlySpendingData(user, currentMonth);

            Map<String, Object> response = new HashMap<>();
            response.put("month", currentMonth.toString());
            response.put("userId", user.getId());
            response.put("currentMonthTotal", data.getCurrentMonthTotal());
            response.put("previousMonthTotal", data.getPreviousMonthTotal());
            response.put("threeMonthAverage", data.getThreeMonthAverage());
            response.put("categoriesCount", data.getCategoryBreakdown().size());
            response.put("trendsCount", data.getTrends().size());
            response.put("budgetsCount", data.getBudgetStatus().size());
            response.put("status", "success");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/spending/detailed/{month}")
    public ResponseEntity<SpendingDataDTO> getDetailedSpendingData(
            @PathVariable String month,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            YearMonth yearMonth = YearMonth.parse(month);
            SpendingDataDTO data = spendingDataAggregator.getMonthlySpendingData(user, yearMonth);

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}