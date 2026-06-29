package org.example.ai.insights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ai.aggregation.SpendingDataAggregator;
import org.example.ai.aggregation.SpendingDataFormatter;
import org.example.ai.cache.InsightCache;
import org.example.config.GeminiClientProvider;
import org.example.config.RateLimitManager;
import org.example.dto.AIInsightResponse;
import org.example.dto.SpendingDataDTO;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class GeminiInsightsService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiInsightsService.class);

    private final GeminiClientProvider geminiClientProvider;
    private final RateLimitManager rateLimitManager;
    private final SpendingDataAggregator spendingDataAggregator;
    private final InsightCache insightCache;
    private final InsightResponseParser responseParser;

    public GeminiInsightsService(GeminiClientProvider geminiClientProvider,
                                 RateLimitManager rateLimitManager,
                                 SpendingDataAggregator spendingDataAggregator,
                                 InsightCache insightCache,
                                 InsightResponseParser responseParser) {
        this.geminiClientProvider = geminiClientProvider;
        this.rateLimitManager = rateLimitManager;
        this.spendingDataAggregator = spendingDataAggregator;
        this.insightCache = insightCache;
        this.responseParser = responseParser;
    }


    public AIInsightResponse generateMonthlyInsights(User user, YearMonth month) {
        logger.info("Generating insights for user {} month {}", user.getId(), month);

        // Check cache first
        Optional<AIInsightResponse> cached = insightCache.get(user.getId(), month);
        if (cached.isPresent()) {
            logger.info("Returning cached insights for user {} month {}", user.getId(), month);
            return cached.get();
        }

        // Check rate limits
        if (!rateLimitManager.canMakeRequest()) {
            logger.warn("Rate limit exceeded, returning template insights for user {}", user.getId());
            return provideTemplateInsights(user, month);
        }

        // Check if Gemini is configured
        if (!geminiClientProvider.isReady()) {
            logger.warn("Gemini not configured, returning template insights for user {}", user.getId());
            return provideTemplateInsights(user, month);
        }

        try {
            // Aggregate spending data
            SpendingDataDTO spendingData = spendingDataAggregator.getMonthlySpendingData(user, month);

            // Build prompt
            String prompt = SpendingDataFormatter.buildInsightPrompt(spendingData);
            logger.debug("Prompt built for user {}: {} characters", user.getId(), prompt.length());

            // Call Gemini API
            AIInsightResponse response = callGeminiAPI(user, month, prompt);

            // Cache the result
            insightCache.put(user.getId(), month, response);

            logger.info("Insights generated successfully for user {} month {}", user.getId(), month);
            return response;

        } catch (Exception e) {
            logger.error("Error generating insights for user {}", user.getId(), e);
            rateLimitManager.recordFailedRequest();
            return provideTemplateInsights(user, month);
        }
    }


    private AIInsightResponse callGeminiAPI(User user, YearMonth month, String prompt) throws Exception {
        logger.debug("Calling Gemini API for user {}", user.getId());

        try {
            WebClient webClient = geminiClientProvider.getWebClient();
            ObjectMapper objectMapper = geminiClientProvider.getObjectMapper();

            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", prompt);
            content.put("parts", new Map[]{part});
            requestBody.put("contents", new Map[]{content});

            // Call API synchronously
            String responseJson = webClient.post()
                    .uri(geminiClientProvider.getApiEndpoint() + "?key=" + geminiClientProvider.getApiKey())
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.debug("Gemini response received for user {}: {} characters", user.getId(), responseJson.length());

            // Parse response
            JsonNode responseNode = objectMapper.readTree(responseJson);
            String responseText = responseNode
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Record usage (estimate if metadata not available)
            int inputTokens = 2000;
            int outputTokens = 500;
            if (responseNode.has("usageMetadata")) {
                JsonNode usage = responseNode.path("usageMetadata");
                inputTokens = usage.path("promptTokenCount").asInt(2000);
                outputTokens = usage.path("candidatesTokenCount").asInt(500);
            }

            rateLimitManager.recordRequest(inputTokens, outputTokens);
            logger.info("API usage - Input: {} tokens, Output: {} tokens", inputTokens, outputTokens);

            // Parse response
            AIInsightResponse insight = responseParser.parseResponse(responseText, month);
            return insight;

        } catch (Exception e) {
            logger.error("Gemini API call failed", e);
            throw e;
        }
    }


    private AIInsightResponse provideTemplateInsights(User user, YearMonth month) {
        logger.info("Generating template insights for user {} month {}", user.getId(), month);

        try {
            SpendingDataDTO spendingData = spendingDataAggregator.getMonthlySpendingData(user, month);

            AIInsightResponse response = new AIInsightResponse();
            response.setMonth(month);

            // Create insights content
            AIInsightResponse.InsightsContent insights = new AIInsightResponse.InsightsContent();

            double percentChange = 0;
            if (spendingData.getPreviousMonthTotal().compareTo(java.math.BigDecimal.ZERO) > 0) {
                percentChange = ((spendingData.getCurrentMonthTotal().doubleValue() -
                        spendingData.getPreviousMonthTotal().doubleValue()) /
                        spendingData.getPreviousMonthTotal().doubleValue()) * 100;
            }

            String trendDirection = percentChange > 5 ? "increased" : percentChange < -5 ? "decreased" : "remained stable";
            insights.setOverallSummary(String.format(
                    "Your spending this month was $%.2f, which %s compared to last month's $%.2f (%.1f%% change). " +
                            "This is %s your 3-month average of $%.2f.",
                    spendingData.getCurrentMonthTotal(),
                    trendDirection,
                    spendingData.getPreviousMonthTotal(),
                    percentChange,
                    percentChange > 0 ? "above" : "below",
                    spendingData.getThreeMonthAverage()
            ));

            insights.setTopConcerns(new ArrayList<>());
            insights.setPositiveAchievements(new ArrayList<>());
            insights.setCategoryInsights(new ArrayList<>());

            response.setInsights(insights);

            // Create recommendations content
            AIInsightResponse.RecommendationsContent recommendations = new AIInsightResponse.RecommendationsContent();
            recommendations.setBudgetAdjustments(new ArrayList<>());
            recommendations.setSavingsOpportunities(new ArrayList<>());
            recommendations.setActionableAdvice(new ArrayList<>());

            response.setRecommendations(recommendations);

            return response;

        } catch (Exception e) {
            logger.error("Error generating template insights", e);
            return createEmptyResponse(month);
        }
    }


    private AIInsightResponse createEmptyResponse(YearMonth month) {
        AIInsightResponse response = new AIInsightResponse();
        response.setMonth(month);
        response.setInsights(new AIInsightResponse.InsightsContent());
        response.setRecommendations(new AIInsightResponse.RecommendationsContent());
        return response;
    }


    public void invalidateCache(Long userId, YearMonth month) {
        logger.info("Invalidating cache for user {} month {}", userId, month);
        insightCache.invalidate(userId, month);
    }


    public Object getCacheStats() {
        return insightCache.getStats();
    }


    public RateLimitManager.TokenUsageStats getRateLimitStats() {
        return rateLimitManager.getUsageStats();
    }
}
