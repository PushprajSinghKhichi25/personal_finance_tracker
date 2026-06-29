package org.example.ai.recommendations;

import org.example.entity.Budget;
import org.example.entity.Category;
import org.example.entity.Expense;
import org.example.entity.User;
import org.example.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


@Service
public class BudgetOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(BudgetOptimizer.class);

    private final ExpenseRepository expenseRepository;
    private final SpendingPatternAnalyzer patternAnalyzer;

    public BudgetOptimizer(ExpenseRepository expenseRepository,
                           SpendingPatternAnalyzer patternAnalyzer) {
        this.expenseRepository = expenseRepository;
        this.patternAnalyzer = patternAnalyzer;
    }


    public BudgetRecommendation recommendBudgetForCategory(
            User user, Category category, int monthsOfHistory, BigDecimal currentBudget) {

        logger.debug("Calculating budget recommendation for category {} with {} months history",
                category.getName(), monthsOfHistory);

        try {
            // Get historical spending for category
            List<BigDecimal> monthlySpending = getHistoricalSpending(user, category, monthsOfHistory);

            if (monthlySpending.isEmpty()) {
                logger.warn("No historical spending found for category {}", category.getName());
                return new BudgetRecommendation(
                        category.getName(),
                        currentBudget,
                        currentBudget,
                        "No historical data available",
                        0.3
                );
            }

            // Calculate statistics
            BigDecimal average = patternAnalyzer.calculateAverageSpending(monthlySpending);
            BigDecimal stdDev = patternAnalyzer.calculateStandardDeviation(monthlySpending);
            BigDecimal percentile90 = patternAnalyzer.calculatePercentile(monthlySpending, 90);
            double volatility = patternAnalyzer.calculateVolatility(monthlySpending);
            SpendingPatternAnalyzer.SpendingTrend trend = patternAnalyzer.analyzeTrend(monthlySpending);

            // Calculate recommended budget
            // Formula: average + (stdDev * 0.5) to provide buffer for variance
            // But at least cover the 90th percentile to avoid frequent overages
            BigDecimal recommendedBudget = average.add(stdDev.multiply(BigDecimal.valueOf(0.5)));
            recommendedBudget = recommendedBudget.max(percentile90);

            // Round to nearest 50
            recommendedBudget = roundToNearestAmount(recommendedBudget, BigDecimal.valueOf(50));

            // Calculate confidence score
            double confidence = calculateConfidence(monthlySpending, stdDev, volatility, trend);

            // Generate reasoning
            String reasoning = generateRecommendationReasoning(
                    category.getName(),
                    average,
                    currentBudget,
                    recommendedBudget,
                    monthlySpending,
                    trend,
                    volatility
            );

            logger.debug("Budget recommendation calculated for {}: current={}, recommended={}, confidence={}",
                    category.getName(), currentBudget, recommendedBudget, confidence);

            return new BudgetRecommendation(
                    category.getName(),
                    currentBudget,
                    recommendedBudget,
                    reasoning,
                    confidence
            );

        } catch (Exception e) {
            logger.error("Error calculating budget recommendation for category {}", category.getName(), e);
            return new BudgetRecommendation(
                    category.getName(),
                    currentBudget,
                    currentBudget,
                    "Error calculating recommendation: " + e.getMessage(),
                    0.0
            );
        }
    }


    private List<BigDecimal> getHistoricalSpending(User user, Category category, int months) {
        List<BigDecimal> monthlySpending = new ArrayList<>();

        YearMonth now = YearMonth.now();
        for (int i = 0; i < months; i++) {
            YearMonth targetMonth = now.minusMonths(i);
            LocalDate monthStart = targetMonth.atDay(1);
            LocalDate monthEnd = targetMonth.atEndOfMonth();

            BigDecimal monthTotal = expenseRepository.findByUserAndDateBetween(user, monthStart, monthEnd)
                    .stream()
                    .filter(e -> e.getCategories().stream()
                            .anyMatch(c -> c.getId().equals(category.getId())))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlySpending.add(monthTotal);
        }

        // Reverse to get chronological order (oldest first)
        java.util.Collections.reverse(monthlySpending);
        return monthlySpending;
    }


    private double calculateConfidence(List<BigDecimal> monthlySpending, BigDecimal stdDev,
                                       double volatility, SpendingPatternAnalyzer.SpendingTrend trend) {

        // More data points = higher confidence
        double dataPointsConfidence = Math.min(monthlySpending.size() / 3.0, 1.0);

        // Lower volatility = higher confidence
        double volatilityConfidence = Math.max(0, 1 - (volatility / 100.0));

        // Stable trend = higher confidence
        double trendConfidence = trend.direction.equals("stable") ? 1.0 : 0.75;

        // Weighted average
        double confidence = (dataPointsConfidence * 0.4) + (volatilityConfidence * 0.35) + (trendConfidence * 0.25);

        return Math.min(1.0, Math.max(0.0, confidence));
    }


    private String generateRecommendationReasoning(String categoryName, BigDecimal average,
                                                   BigDecimal current, BigDecimal recommended,
                                                   List<BigDecimal> monthlySpending,
                                                   SpendingPatternAnalyzer.SpendingTrend trend,
                                                   double volatility) {

        StringBuilder reasoning = new StringBuilder();

        reasoning.append("Based on your spending pattern over the last ")
                .append(monthlySpending.size())
                .append(" months, you've averaged $")
                .append(String.format("%.2f", average))
                .append(" in this category. ");

        if (recommended.compareTo(current) > 0) {
            BigDecimal increase = recommended.subtract(current);
            double percentIncrease = (increase.doubleValue() / current.doubleValue()) * 100;
            reasoning.append("Your spending has been consistently above your current budget of $")
                    .append(String.format("%.2f", current))
                    .append(". We recommend increasing to $")
                    .append(String.format("%.2f", recommended))
                    .append(" (a ")
                    .append(String.format("%.0f%%", percentIncrease))
                    .append(" increase) to better accommodate your actual spending patterns.");
        } else if (recommended.compareTo(current) < 0) {
            BigDecimal decrease = current.subtract(recommended);
            double percentDecrease = (decrease.doubleValue() / current.doubleValue()) * 100;
            reasoning.append("You're averaging only $")
                    .append(String.format("%.2f", average))
                    .append(" per month, suggesting your budget of $")
                    .append(String.format("%.2f", current))
                    .append(" could be reduced to $")
                    .append(String.format("%.2f", recommended))
                    .append(" (a ")
                    .append(String.format("%.0f%%", percentDecrease))
                    .append(" reduction).");
        } else {
            reasoning.append("Your current budget of $")
                    .append(String.format("%.2f", current))
                    .append(" aligns well with your historical average of $")
                    .append(String.format("%.2f", average))
                    .append(".");
        }

        if (trend.direction.equals("increasing")) {
            reasoning.append(" Note: This category shows an increasing trend, which may warrant further monitoring.");
        } else if (trend.direction.equals("decreasing")) {
            reasoning.append(" Your spending in this category is decreasing, which is positive.");
        }

        if (volatility > 30) {
            reasoning.append(" The high variability (")
                    .append(String.format("%.0f%%", volatility))
                    .append(") suggests you may want to keep a buffer or monitor this category closely.");
        }

        return reasoning.toString();
    }


    private BigDecimal roundToNearestAmount(BigDecimal value, BigDecimal roundTo) {
        return value.divide(roundTo, 0, RoundingMode.HALF_UP).multiply(roundTo);
    }


    public static class BudgetRecommendation {
        public final String category;
        public final BigDecimal currentBudget;
        public final BigDecimal recommendedBudget;
        public final String reasoning;
        public final double confidence;

        public BudgetRecommendation(String category, BigDecimal currentBudget,
                                    BigDecimal recommendedBudget, String reasoning, double confidence) {
            this.category = category;
            this.currentBudget = currentBudget;
            this.recommendedBudget = recommendedBudget;
            this.reasoning = reasoning;
            this.confidence = confidence;
        }

        @Override
        public String toString() {
            return String.format("BudgetRec{%s: $%.2f→$%.2f, conf=%.2f}",
                    category, currentBudget, recommendedBudget, confidence);
        }
    }
}