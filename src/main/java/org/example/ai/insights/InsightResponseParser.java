package org.example.ai.insights;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AIInsightResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Component
public class InsightResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(InsightResponseParser.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();



    public AIInsightResponse parseResponse(String responseText, YearMonth month) {
        logger.debug("Parsing Gemini response for month {}", month);

        try {
            // Extract JSON from response
            String jsonStr = extractJson(responseText);

            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(jsonStr);

            // Create response object
            AIInsightResponse response = new AIInsightResponse();
            response.setMonth(month);

            // Parse insights
            response.setInsights(parseInsights(rootNode.get("insights")));

            // Parse recommendations
            response.setRecommendations(parseRecommendations(rootNode.get("recommendations")));

            logger.debug("Response parsed successfully for month {}", month);
            return response;

        } catch (Exception e) {
            logger.error("Error parsing Gemini response", e);
            // Return empty response instead of failing
            return createEmptyResponse(month);
        }
    }


    private String extractJson(String responseText) {
        String text = responseText.trim();

        // Remove markdown code blocks if present
        if (text.startsWith("```json")) {
            text = text.substring(7); // Remove ```json
        } else if (text.startsWith("```")) {
            text = text.substring(3); // Remove ```
        }

        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3); // Remove trailing ```
        }

        return text.trim();
    }


    private AIInsightResponse.InsightsContent parseInsights(JsonNode insightsNode) {
        AIInsightResponse.InsightsContent insights = new AIInsightResponse.InsightsContent();

        if (insightsNode == null) {
            return insights;
        }

        // Parse overall summary
        if (insightsNode.has("overallSummary")) {
            insights.setOverallSummary(insightsNode.get("overallSummary").asText(""));
        }

        // Parse category insights
        List<AIInsightResponse.CategoryInsight> categoryInsights = new ArrayList<>();
        if (insightsNode.has("categoryInsights")) {
            JsonNode categories = insightsNode.get("categoryInsights");
            if (categories.isArray()) {
                for (JsonNode catNode : categories) {
                    categoryInsights.add(parseCategoryInsight(catNode));
                }
            }
        }
        insights.setCategoryInsights(categoryInsights);

        // Parse top concerns
        List<String> topConcerns = new ArrayList<>();
        if (insightsNode.has("topConcerns")) {
            JsonNode concerns = insightsNode.get("topConcerns");
            if (concerns.isArray()) {
                for (JsonNode concern : concerns) {
                    topConcerns.add(concern.asText(""));
                }
            }
        }
        insights.setTopConcerns(topConcerns);

        // Parse positive achievements
        List<String> achievements = new ArrayList<>();
        if (insightsNode.has("positiveAchievements")) {
            JsonNode achievementNodes = insightsNode.get("positiveAchievements");
            if (achievementNodes.isArray()) {
                for (JsonNode achievement : achievementNodes) {
                    achievements.add(achievement.asText(""));
                }
            }
        }
        insights.setPositiveAchievements(achievements);

        return insights;
    }


    private AIInsightResponse.CategoryInsight parseCategoryInsight(JsonNode catNode) {
        AIInsightResponse.CategoryInsight insight = new AIInsightResponse.CategoryInsight();

        insight.setCategory(catNode.get("category").asText(""));
        insight.setAmount(new BigDecimal(catNode.get("amount").asDouble(0)));
        insight.setPercentOfTotal(catNode.get("percentOfTotal").asDouble(0));
        insight.setTrend(catNode.get("trend").asText("stable"));
        insight.setPercentChange(catNode.get("percentChange").asDouble(0));
        insight.setInsight(catNode.get("insight").asText(""));

        // Parse budget status if present
        if (catNode.has("budgetStatus")) {
            insight.setBudgetStatus(parseBudgetInsight(catNode.get("budgetStatus")));
        }

        return insight;
    }


    private AIInsightResponse.BudgetInsight parseBudgetInsight(JsonNode budgetNode) {
        AIInsightResponse.BudgetInsight insight = new AIInsightResponse.BudgetInsight();

        insight.setAllocated(new BigDecimal(budgetNode.get("allocated").asDouble(0)));
        insight.setSpent(new BigDecimal(budgetNode.get("spent").asDouble(0)));

        if (budgetNode.has("overage")) {
            insight.setOverage(new BigDecimal(budgetNode.get("overage").asDouble(0)));
        } else {
            insight.setOverage(BigDecimal.ZERO);
        }

        insight.setInsight(budgetNode.get("insight").asText(""));

        return insight;
    }


    private AIInsightResponse.RecommendationsContent parseRecommendations(JsonNode recommendationsNode) {
        AIInsightResponse.RecommendationsContent recommendations = new AIInsightResponse.RecommendationsContent();

        if (recommendationsNode == null) {
            return recommendations;
        }

        // Parse budget adjustments
        List<AIInsightResponse.BudgetRecommendation> adjustments = new ArrayList<>();
        if (recommendationsNode.has("budgetAdjustments")) {
            JsonNode adjustmentsNode = recommendationsNode.get("budgetAdjustments");
            if (adjustmentsNode.isArray()) {
                for (JsonNode adjNode : adjustmentsNode) {
                    adjustments.add(parseBudgetRecommendation(adjNode));
                }
            }
        }
        recommendations.setBudgetAdjustments(adjustments);

        // Parse savings opportunities
        List<String> opportunities = new ArrayList<>();
        if (recommendationsNode.has("savingsOpportunities")) {
            JsonNode opportunitiesNode = recommendationsNode.get("savingsOpportunities");
            if (opportunitiesNode.isArray()) {
                for (JsonNode opp : opportunitiesNode) {
                    opportunities.add(opp.asText(""));
                }
            }
        }
        recommendations.setSavingsOpportunities(opportunities);

        // Parse actionable advice
        List<String> advice = new ArrayList<>();
        if (recommendationsNode.has("actionableAdvice")) {
            JsonNode adviceNode = recommendationsNode.get("actionableAdvice");
            if (adviceNode.isArray()) {
                for (JsonNode adv : adviceNode) {
                    advice.add(adv.asText(""));
                }
            }
        }
        recommendations.setActionableAdvice(advice);

        return recommendations;
    }


    private AIInsightResponse.BudgetRecommendation parseBudgetRecommendation(JsonNode recNode) {
        AIInsightResponse.BudgetRecommendation recommendation = new AIInsightResponse.BudgetRecommendation();

        recommendation.setCategory(recNode.get("category").asText(""));
        recommendation.setCurrentBudget(new BigDecimal(recNode.get("currentBudget").asDouble(0)));
        recommendation.setRecommendedBudget(new BigDecimal(recNode.get("recommendedBudget").asDouble(0)));
        recommendation.setReasoning(recNode.get("reasoning").asText(""));
        recommendation.setConfidence(recNode.get("confidence").asDouble(0));

        return recommendation;
    }


    private AIInsightResponse createEmptyResponse(YearMonth month) {
        AIInsightResponse response = new AIInsightResponse();
        response.setMonth(month);
        response.setInsights(new AIInsightResponse.InsightsContent());
        response.setRecommendations(new AIInsightResponse.RecommendationsContent());
        return response;
    }
}
