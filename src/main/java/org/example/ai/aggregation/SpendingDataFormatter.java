package org.example.ai.aggregation;

import org.example.dto.SpendingDataDTO;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;


public class SpendingDataFormatter {


    public static String formatCategoryBreakdown(Map<String, SpendingDataDTO.CategorySpendingDTO> breakdown) {
        if (breakdown == null || breakdown.isEmpty()) {
            return "No spending data available";
        }

        return breakdown.values().stream()
                .map(cat -> String.format(
                        "- %s: $%.2f (%.1f%% of total, %s %.1f%%)",
                        cap(cat.getCategoryName()),
                        cat.getAmount(),
                        cat.getPercentOfTotal(),
                        cat.getPercentChange() > 0 ? "↑" : cat.getPercentChange() < 0 ? "↓" : "→",
                        Math.abs(cat.getPercentChange())
                ))
                .collect(Collectors.joining("\n"));
    }


    public static String formatBudgetStatus(java.util.List<SpendingDataDTO.BudgetStatusDTO> budgets) {
        if (budgets == null || budgets.isEmpty()) {
            return "No budgets set";
        }

        return budgets.stream()
                .map(b -> String.format(
                        "- %s: Allocated $%.2f, Spent $%.2f (%.1f%%)%s",
                        cap(b.getCategoryName()),
                        b.getAllocated(),
                        b.getSpent(),
                        b.getPercentageUsed(),
                        b.isOverBudget() ? String.format(" [OVER by $%.2f]", b.getSpent().subtract(b.getAllocated())) : ""
                ))
                .collect(Collectors.joining("\n"));
    }


    public static String formatMonthlyTrends(java.util.List<SpendingDataDTO.MonthlyTrendDTO> trends) {
        if (trends == null || trends.isEmpty()) {
            return "No trend data";
        }

        return trends.stream()
                .map(t -> String.format(
                        "- %s: $%.2f",
                        t.getMonth(),
                        t.getTotal()
                ))
                .collect(Collectors.joining("\n"));
    }


    public static String buildInsightPrompt(SpendingDataDTO data) {
        return String.format("""
                You are a personal finance advisor AI. Analyze the following spending data and provide insights and recommendations.

                CURRENT MONTH: %s

                SPENDING SUMMARY:
                - Current Month Total: $%.2f
                - Previous Month Total: $%.2f
                - 3-Month Average: $%.2f
                - Month-over-Month Change: %.1f%%

                CATEGORY BREAKDOWN (Current Month):
                %s

                MONTHLY TRENDS (Last 3 Months):
                %s

                BUDGET STATUS:
                %s

                Please provide your analysis in the following JSON format:
                {
                  "overallSummary": "A comprehensive summary of spending patterns with specific numbers",
                  "categoryInsights": [
                    {
                      "category": "category_name",
                      "amount": 1200.00,
                      "percentOfTotal": 35,
                      "trend": "increased|decreased|stable",
                      "percentChange": 8.5,
                      "budgetStatus": {
                        "allocated": 1000,
                        "spent": 1200,
                        "overage": 200,
                        "insight": "Specific insight about this category"
                      },
                      "insight": "Detailed insight about this category"
                    }
                  ],
                  "topConcerns": ["concern 1", "concern 2", "concern 3"],
                  "positiveAchievements": ["achievement 1", "achievement 2"],
                  "budgetAdjustments": [
                    {
                      "category": "category_name",
                      "currentBudget": 1000,
                      "recommendedBudget": 1250,
                      "reasoning": "Detailed reasoning based on 3-month pattern",
                      "confidence": 0.92
                    }
                  ],
                  "savingsOpportunities": ["opportunity 1", "opportunity 2"],
                  "actionableAdvice": ["action 1", "action 2"]
                }

                Guidelines:
                - Be specific with numbers and percentages
                - Provide actionable recommendations
                - Focus on patterns from the last 3 months
                - Highlight both concerns and positive trends
                - Confidence scores should be between 0.0 and 1.0
                """,
                data.getMonth(),
                data.getCurrentMonthTotal(),
                data.getPreviousMonthTotal(),
                data.getThreeMonthAverage(),
                TrendCalculator.calculatePercentChange(data.getPreviousMonthTotal(), data.getCurrentMonthTotal()),
                formatCategoryBreakdown(data.getCategoryBreakdown()),
                formatMonthlyTrends(data.getTrends()),
                formatBudgetStatus(data.getBudgetStatus())
        );
    }


    private static String cap(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
