package org.example.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


public class AIInsightResponse {

    private YearMonth month;
    private LocalDateTime generatedAt;
    private InsightsContent insights;
    private RecommendationsContent recommendations;

    public AIInsightResponse() {
        this.generatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public InsightsContent getInsights() {
        return insights;
    }

    public void setInsights(InsightsContent insights) {
        this.insights = insights;
    }

    public RecommendationsContent getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(RecommendationsContent recommendations) {
        this.recommendations = recommendations;
    }


    public static class InsightsContent {
        private String overallSummary;
        private List<CategoryInsight> categoryInsights = new ArrayList<>();
        private List<String> topConcerns = new ArrayList<>();
        private List<String> positiveAchievements = new ArrayList<>();

        public String getOverallSummary() {
            return overallSummary;
        }

        public void setOverallSummary(String overallSummary) {
            this.overallSummary = overallSummary;
        }

        public List<CategoryInsight> getCategoryInsights() {
            return categoryInsights;
        }

        public void setCategoryInsights(List<CategoryInsight> categoryInsights) {
            this.categoryInsights = categoryInsights;
        }

        public List<String> getTopConcerns() {
            return topConcerns;
        }

        public void setTopConcerns(List<String> topConcerns) {
            this.topConcerns = topConcerns;
        }

        public List<String> getPositiveAchievements() {
            return positiveAchievements;
        }

        public void setPositiveAchievements(List<String> positiveAchievements) {
            this.positiveAchievements = positiveAchievements;
        }
    }


    public static class CategoryInsight {
        private String category;
        private BigDecimal amount;
        private double percentOfTotal;
        private String trend; // "increased", "decreased", "stable"
        private double percentChange;
        private BudgetInsight budgetStatus;
        private String insight;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public double getPercentOfTotal() {
            return percentOfTotal;
        }

        public void setPercentOfTotal(double percentOfTotal) {
            this.percentOfTotal = percentOfTotal;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }

        public double getPercentChange() {
            return percentChange;
        }

        public void setPercentChange(double percentChange) {
            this.percentChange = percentChange;
        }

        public BudgetInsight getBudgetStatus() {
            return budgetStatus;
        }

        public void setBudgetStatus(BudgetInsight budgetStatus) {
            this.budgetStatus = budgetStatus;
        }

        public String getInsight() {
            return insight;
        }

        public void setInsight(String insight) {
            this.insight = insight;
        }
    }


    public static class BudgetInsight {
        private BigDecimal allocated;
        private BigDecimal spent;
        private BigDecimal overage;
        private String insight;

        public BigDecimal getAllocated() {
            return allocated;
        }

        public void setAllocated(BigDecimal allocated) {
            this.allocated = allocated;
        }

        public BigDecimal getSpent() {
            return spent;
        }

        public void setSpent(BigDecimal spent) {
            this.spent = spent;
        }

        public BigDecimal getOverage() {
            return overage;
        }

        public void setOverage(BigDecimal overage) {
            this.overage = overage;
        }

        public String getInsight() {
            return insight;
        }

        public void setInsight(String insight) {
            this.insight = insight;
        }
    }


    public static class RecommendationsContent {
        private List<BudgetRecommendation> budgetAdjustments = new ArrayList<>();
        private List<String> savingsOpportunities = new ArrayList<>();
        private List<String> actionableAdvice = new ArrayList<>();

        public List<BudgetRecommendation> getBudgetAdjustments() {
            return budgetAdjustments;
        }

        public void setBudgetAdjustments(List<BudgetRecommendation> budgetAdjustments) {
            this.budgetAdjustments = budgetAdjustments;
        }

        public List<String> getSavingsOpportunities() {
            return savingsOpportunities;
        }

        public void setSavingsOpportunities(List<String> savingsOpportunities) {
            this.savingsOpportunities = savingsOpportunities;
        }

        public List<String> getActionableAdvice() {
            return actionableAdvice;
        }

        public void setActionableAdvice(List<String> actionableAdvice) {
            this.actionableAdvice = actionableAdvice;
        }
    }


    public static class BudgetRecommendation {
        private String category;
        private BigDecimal currentBudget;
        private BigDecimal recommendedBudget;
        private String reasoning;
        private double confidence; // 0.0 to 1.0

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getCurrentBudget() {
            return currentBudget;
        }

        public void setCurrentBudget(BigDecimal currentBudget) {
            this.currentBudget = currentBudget;
        }

        public BigDecimal getRecommendedBudget() {
            return recommendedBudget;
        }

        public void setRecommendedBudget(BigDecimal recommendedBudget) {
            this.recommendedBudget = recommendedBudget;
        }

        public String getReasoning() {
            return reasoning;
        }

        public void setReasoning(String reasoning) {
            this.reasoning = reasoning;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
    }
}