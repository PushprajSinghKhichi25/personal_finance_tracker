package org.example.controller;

import org.example.ai.recommendations.BudgetOptimizer;
import org.example.ai.recommendations.BudgetRecommendationService;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/ai/recommendations")
public class BudgetRecommendationsController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetRecommendationsController.class);

    private final BudgetRecommendationService budgetRecommendationService;
    private final UserRepository userRepository;

    public BudgetRecommendationsController(BudgetRecommendationService budgetRecommendationService,
                                           UserRepository userRepository) {
        this.budgetRecommendationService = budgetRecommendationService;
        this.userRepository = userRepository;
    }


    @GetMapping("/budgets")
    public ResponseEntity<Map<String, Object>> getBudgetRecommendations(
            @RequestParam(required = false, defaultValue = "3") int months,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("Fetching budget recommendations for user {} with {} months history",
                    user.getId(), months);

            List<BudgetOptimizer.BudgetRecommendation> recommendations =
                    budgetRecommendationService.getRecommendationsForUser(user, months);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("monthsAnalyzed", months);
            response.put("recommendationCount", recommendations.size());
            response.put("recommendations", recommendations.stream()
                    .map(this::mapRecommendation)
                    .toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching budget recommendations", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/high-priority")
    public ResponseEntity<Map<String, Object>> getHighPriorityRecommendations(
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("Fetching high priority recommendations for user {}", user.getId());

            List<BudgetOptimizer.BudgetRecommendation> recommendations =
                    budgetRecommendationService.getHighPriorityRecommendations(user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", recommendations.size());
            response.put("recommendations", recommendations.stream()
                    .map(this::mapRecommendation)
                    .toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching high priority recommendations", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/savings")
    public ResponseEntity<Map<String, Object>> getSavingsOpportunities(
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("Fetching savings opportunities for user {}", user.getId());

            List<BudgetOptimizer.BudgetRecommendation> recommendations =
                    budgetRecommendationService.getSavingsOpportunities(user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", recommendations.size());
            response.put("recommendations", recommendations.stream()
                    .map(this::mapRecommendation)
                    .toList());

            // Calculate total potential savings
            double totalSavings = recommendations.stream()
                    .mapToDouble(rec -> rec.currentBudget.subtract(rec.recommendedBudget).doubleValue())
                    .sum();

            response.put("totalPotentialSavings", String.format("$%.2f", totalSavings));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching savings opportunities", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/increases")
    public ResponseEntity<Map<String, Object>> getBudgetIncreaseRecommendations(
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.info("Fetching budget increase recommendations for user {}", user.getId());

            List<BudgetOptimizer.BudgetRecommendation> recommendations =
                    budgetRecommendationService.getBudgetIncreaseRecommendations(user);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", recommendations.size());
            response.put("recommendations", recommendations.stream()
                    .map(this::mapRecommendation)
                    .toList());

            // Calculate total increase needed
            double totalIncrease = recommendations.stream()
                    .mapToDouble(rec -> rec.recommendedBudget.subtract(rec.currentBudget).doubleValue())
                    .sum();

            response.put("totalIncreasedNeeded", String.format("$%.2f", totalIncrease));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching budget increase recommendations", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    private Map<String, Object> mapRecommendation(BudgetOptimizer.BudgetRecommendation rec) {
        Map<String, Object> map = new HashMap<>();
        map.put("category", rec.category);
        map.put("currentBudget", String.format("$%.2f", rec.currentBudget));
        map.put("recommendedBudget", String.format("$%.2f", rec.recommendedBudget));

        double change = ((rec.recommendedBudget.doubleValue() - rec.currentBudget.doubleValue()) /
                rec.currentBudget.doubleValue()) * 100;
        map.put("percentageChange", String.format("%.1f%%", change));

        map.put("confidence", String.format("%.1f%%", rec.confidence * 100));
        map.put("reasoning", rec.reasoning);

        return map;
    }
}
