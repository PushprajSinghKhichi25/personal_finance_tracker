package org.example.controller;

import org.example.ai.insights.GeminiInsightsService;
import org.example.config.RateLimitManager;
import org.example.dto.AIInsightResponse;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/ai/insights")
public class AIInsightsController {

    private static final Logger logger = LoggerFactory.getLogger(AIInsightsController.class);

    private final GeminiInsightsService geminiInsightsService;
    private final UserRepository userRepository;

    public AIInsightsController(GeminiInsightsService geminiInsightsService,
                                UserRepository userRepository) {
        this.geminiInsightsService = geminiInsightsService;
        this.userRepository = userRepository;
    }


    @GetMapping("/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyInsights(
            @RequestParam(required = false) String month,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Use current month if not specified
            YearMonth targetMonth = month != null ? YearMonth.parse(month) : YearMonth.now();

            logger.info("Fetching insights for user {} month {}", user.getId(), targetMonth);

            // Generate insights
            AIInsightResponse insights = geminiInsightsService.generateMonthlyInsights(user, targetMonth);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("month", targetMonth.toString());
            response.put("insights", insights);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching insights", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentMonthInsights(Authentication authentication) {
        return getMonthlyInsights(null, authentication);
    }


    @PostMapping("/invalidate")
    public ResponseEntity<Map<String, Object>> invalidateInsights(
            @RequestParam String month,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            YearMonth targetMonth = YearMonth.parse(month);
            geminiInsightsService.invalidateCache(user.getId(), targetMonth);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cache invalidated for " + month);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error invalidating cache", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/cache-stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        try {
            Object stats = geminiInsightsService.getCacheStats();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("cacheStats", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching cache stats", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/usage-stats")
    public ResponseEntity<Map<String, Object>> getUsageStats() {
        try {
            RateLimitManager.TokenUsageStats stats = geminiInsightsService.getRateLimitStats();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("requestsThisMinute", stats.requestsThisMinute);
            response.put("maxRequestsPerMinute", stats.maxRequestsPerMinute);
            response.put("requestUsagePercentage", String.format("%.1f%%", stats.getRequestUsagePercentage()));
            response.put("tokensUsedThisMonth", stats.tokensUsedThisMonth);
            response.put("maxTokensPerMonth", stats.maxTokensPerMonth);
            response.put("tokenUsagePercentage", String.format("%.1f%%", stats.getTokenUsagePercentage()));
            response.put("estimatedTokensPerRequest", stats.estimatedTokensPerRequest);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error fetching usage stats", e);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}