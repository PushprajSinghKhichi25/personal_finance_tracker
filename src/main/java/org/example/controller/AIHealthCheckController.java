package org.example.controller;

import org.example.config.GeminiClientProvider;
import org.example.config.RateLimitManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/ai")
public class AIHealthCheckController {

    private final GeminiClientProvider geminiClientProvider;
    private final RateLimitManager rateLimitManager;

    public AIHealthCheckController(GeminiClientProvider geminiClientProvider,
                                   RateLimitManager rateLimitManager) {
        this.geminiClientProvider = geminiClientProvider;
        this.rateLimitManager = rateLimitManager;
    }


    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();

        boolean isReady = geminiClientProvider.isReady();
        response.put("status", isReady ? "ready" : "not_configured");
        response.put("provider", "google-gemini");
        response.put("model", geminiClientProvider.getModel());
        response.put("configured", isReady);

        if (!isReady) {
            response.put("message", "Gemini API key not configured. Set GOOGLE_API_KEY environment variable.");
            return ResponseEntity.ok(response);
        }

        response.put("message", "Gemini AI service is ready");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/usage-stats")
    public ResponseEntity<Map<String, Object>> getUsageStats() {
        RateLimitManager.TokenUsageStats stats = rateLimitManager.getUsageStats();

        Map<String, Object> response = new HashMap<>();
        response.put("requestsThisMinute", stats.requestsThisMinute);
        response.put("maxRequestsPerMinute", stats.maxRequestsPerMinute);
        response.put("requestUsagePercentage", String.format("%.1f%%", stats.getRequestUsagePercentage()));
        response.put("tokensUsedThisMonth", stats.tokensUsedThisMonth);
        response.put("maxTokensPerMonth", stats.maxTokensPerMonth);
        response.put("tokenUsagePercentage", String.format("%.1f%%", stats.getTokenUsagePercentage()));
        response.put("estimatedTokensPerRequest", stats.estimatedTokensPerRequest);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/can-request")
    public ResponseEntity<Map<String, Object>> canMakeRequest() {
        boolean canRequest = rateLimitManager.canMakeRequest();
        RateLimitManager.TokenUsageStats stats = rateLimitManager.getUsageStats();

        Map<String, Object> response = new HashMap<>();
        response.put("canMakeRequest", canRequest);
        response.put("reason", canRequest ? "OK" : "Rate limit or token limit exceeded");
        response.put("stats", Map.of(
                "requestsThisMinute", stats.requestsThisMinute,
                "tokensUsedThisMonth", stats.tokensUsedThisMonth,
                "percentTokensUsed", String.format("%.1f%%", stats.getTokenUsagePercentage())
        ));

        return ResponseEntity.ok(response);
    }
}
