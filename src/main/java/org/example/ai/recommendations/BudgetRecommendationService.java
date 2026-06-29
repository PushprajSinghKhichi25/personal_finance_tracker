package org.example.ai.recommendations;

import org.example.entity.Budget;
import org.example.entity.User;
import org.example.repository.BudgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


@Service
public class BudgetRecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(BudgetRecommendationService.class);
    private static final int DEFAULT_MONTHS_HISTORY = 3;

    private final BudgetOptimizer budgetOptimizer;
    private final BudgetRepository budgetRepository;

    public BudgetRecommendationService(BudgetOptimizer budgetOptimizer,
                                       BudgetRepository budgetRepository) {
        this.budgetOptimizer = budgetOptimizer;
        this.budgetRepository = budgetRepository;
    }


    public List<BudgetOptimizer.BudgetRecommendation> getRecommendationsForUser(
            User user, int monthsOfHistory) {

        logger.info("Generating budget recommendations for user {} with {} months history",
                user.getId(), monthsOfHistory);

        List<BudgetOptimizer.BudgetRecommendation> recommendations = new ArrayList<>();

        try {
            // Get all active budgets for current month
            String currentMonth = YearMonth.now().toString();
            List<Budget> budgets = budgetRepository.findByUserAndCurrentMonth(user, currentMonth);

            if (budgets.isEmpty()) {
                logger.warn("No budgets found for user {} current month", user.getId());
                return recommendations;
            }

            // Generate recommendation for each budget
            for (Budget budget : budgets) {
                BudgetOptimizer.BudgetRecommendation recommendation =
                        budgetOptimizer.recommendBudgetForCategory(
                                user,
                                budget.getCategory(),
                                monthsOfHistory,
                                budget.getMonthlyLimit()
                        );
                recommendations.add(recommendation);
            }

            logger.info("Generated {} recommendations for user {}", recommendations.size(), user.getId());
            return recommendations;

        } catch (Exception e) {
            logger.error("Error generating recommendations for user {}", user.getId(), e);
            return recommendations;
        }
    }


    public List<BudgetOptimizer.BudgetRecommendation> getRecommendationsForUser(User user) {
        return getRecommendationsForUser(user, DEFAULT_MONTHS_HISTORY);
    }


    public List<BudgetOptimizer.BudgetRecommendation> getHighPriorityRecommendations(User user) {
        return getRecommendationsForUser(user, DEFAULT_MONTHS_HISTORY).stream()
                .filter(rec -> rec.confidence >= 0.7) // High confidence
                .filter(rec -> Math.abs(rec.recommendedBudget.doubleValue() -
                        rec.currentBudget.doubleValue()) /
                        rec.currentBudget.doubleValue() > 0.1) // At least 10% change
                .toList();
    }

    public List<BudgetOptimizer.BudgetRecommendation> getSavingsOpportunities(User user) {
        return getRecommendationsForUser(user, DEFAULT_MONTHS_HISTORY).stream()
                .filter(rec -> rec.recommendedBudget.compareTo(rec.currentBudget) < 0)
                .toList();
    }


    public List<BudgetOptimizer.BudgetRecommendation> getBudgetIncreaseRecommendations(User user) {
        return getRecommendationsForUser(user, DEFAULT_MONTHS_HISTORY).stream()
                .filter(rec -> rec.recommendedBudget.compareTo(rec.currentBudget) > 0)
                .toList();
    }
}
