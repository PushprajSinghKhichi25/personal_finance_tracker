package org.example.ai.insights;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AIInsightResponse;
import org.example.entity.AIInsight;
import org.example.entity.User;
import org.example.repository.AIInsightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;


@Service
public class AIInsightPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(AIInsightPersistenceService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final AIInsightRepository aiInsightRepository;

    public AIInsightPersistenceService(AIInsightRepository aiInsightRepository) {
        this.aiInsightRepository = aiInsightRepository;
    }


    @Transactional
    public AIInsight saveInsight(User user, AIInsightResponse insightResponse) {
        try {
            String month = insightResponse.getMonth().toString();

            // Check if insight already exists for this month
            Optional<AIInsight> existing = aiInsightRepository.findByUserAndMonth(user, month);
            if (existing.isPresent()) {
                logger.debug("Updating existing insight for user {} month {}", user.getId(), month);
                AIInsight insight = existing.get();
                insight.setInsightsJson(objectMapper.writeValueAsString(insightResponse.getInsights()));
                insight.setRecommendationsJson(objectMapper.writeValueAsString(insightResponse.getRecommendations()));
                insight.setGeneratedAt(insightResponse.getGeneratedAt());
                insight.setUpdatedAt(LocalDateTime.now());
                return aiInsightRepository.save(insight);
            }

            // Create new insight
            AIInsight insight = AIInsight.builder()
                    .user(user)
                    .month(month)
                    .insightsJson(objectMapper.writeValueAsString(insightResponse.getInsights()))
                    .recommendationsJson(objectMapper.writeValueAsString(insightResponse.getRecommendations()))
                    .generatedAt(insightResponse.getGeneratedAt())
                    .cachedAt(LocalDateTime.now())
                    .build();

            AIInsight saved = aiInsightRepository.save(insight);
            logger.info("Insight saved for user {} month {}", user.getId(), month);
            return saved;

        } catch (Exception e) {
            logger.error("Error saving insight", e);
            throw new RuntimeException("Failed to save insight", e);
        }
    }


    public Optional<AIInsight> getInsight(User user, YearMonth month) {
        return aiInsightRepository.findByUserAndMonth(user, month.toString());
    }


    public List<AIInsight> getUserInsights(User user) {
        return aiInsightRepository.findByUserOrderByMonthDesc(user);
    }


    @Transactional
    public void deleteInsight(User user, YearMonth month) {
        aiInsightRepository.deleteByUserAndMonth(user, month.toString());
        logger.info("Insight deleted for user {} month {}", user.getId(), month);
    }


    public AIInsightResponse reconstructResponse(AIInsight insight) {
        try {
            AIInsightResponse response = new AIInsightResponse();
            response.setMonth(YearMonth.parse(insight.getMonth()));
            response.setGeneratedAt(insight.getGeneratedAt());

            // Parse insights JSON
            AIInsightResponse.InsightsContent insightsContent = objectMapper.readValue(
                    insight.getInsightsJson(),
                    AIInsightResponse.InsightsContent.class
            );
            response.setInsights(insightsContent);

            // Parse recommendations JSON
            AIInsightResponse.RecommendationsContent recommendationsContent = objectMapper.readValue(
                    insight.getRecommendationsJson(),
                    AIInsightResponse.RecommendationsContent.class
            );
            response.setRecommendations(recommendationsContent);

            return response;

        } catch (Exception e) {
            logger.error("Error reconstructing response from stored insight", e);
            throw new RuntimeException("Failed to reconstruct insight response", e);
        }
    }
}
