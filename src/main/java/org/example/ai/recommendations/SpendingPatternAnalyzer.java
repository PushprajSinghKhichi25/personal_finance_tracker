package org.example.ai.recommendations;

import org.example.dto.SpendingDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@Component
public class SpendingPatternAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(SpendingPatternAnalyzer.class);


    public BigDecimal calculateAverageSpending(List<BigDecimal> monthlyAmounts) {
        if (monthlyAmounts == null || monthlyAmounts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = monthlyAmounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(monthlyAmounts.size()), 2, RoundingMode.HALF_UP);
    }


    public BigDecimal calculateStandardDeviation(List<BigDecimal> monthlyAmounts) {
        if (monthlyAmounts == null || monthlyAmounts.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal average = calculateAverageSpending(monthlyAmounts);

        BigDecimal sumOfSquares = monthlyAmounts.stream()
                .map(amount -> amount.subtract(average).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal variance = sumOfSquares.divide(
                BigDecimal.valueOf(monthlyAmounts.size() - 1),
                4,
                RoundingMode.HALF_UP
        );

        double stdDev = Math.sqrt(variance.doubleValue());
        return new BigDecimal(stdDev).setScale(2, RoundingMode.HALF_UP);
    }


    public BigDecimal calculatePercentile(List<BigDecimal> monthlyAmounts, int percentile) {
        if (monthlyAmounts == null || monthlyAmounts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> sorted = new ArrayList<>(monthlyAmounts);
        sorted.sort(BigDecimal::compareTo);

        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));

        return sorted.get(index);
    }


    public SpendingTrend analyzeTrend(List<BigDecimal> monthlyAmounts) {
        if (monthlyAmounts == null || monthlyAmounts.size() < 2) {
            return new SpendingTrend("stable", 0, 0);
        }

        BigDecimal first = monthlyAmounts.get(0);
        BigDecimal last = monthlyAmounts.get(monthlyAmounts.size() - 1);

        if (first.compareTo(BigDecimal.ZERO) == 0) {
            return new SpendingTrend("stable", 0, 0);
        }

        BigDecimal change = last.subtract(first);
        double percentChange = (change.doubleValue() / first.doubleValue()) * 100;

        String direction = percentChange > 5 ? "increasing" : percentChange < -5 ? "decreasing" : "stable";

        return new SpendingTrend(direction, percentChange, last.subtract(first).doubleValue());
    }


    public double calculateVolatility(List<BigDecimal> monthlyAmounts) {
        if (monthlyAmounts == null || monthlyAmounts.isEmpty()) {
            return 0;
        }

        BigDecimal average = calculateAverageSpending(monthlyAmounts);
        BigDecimal stdDev = calculateStandardDeviation(monthlyAmounts);

        if (average.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        return (stdDev.doubleValue() / average.doubleValue()) * 100;
    }


    public static class SpendingTrend {
        public final String direction; // "increasing", "decreasing", "stable"
        public final double percentChange;
        public final double absoluteChange;

        public SpendingTrend(String direction, double percentChange, double absoluteChange) {
            this.direction = direction;
            this.percentChange = percentChange;
            this.absoluteChange = absoluteChange;
        }

        @Override
        public String toString() {
            return String.format("Trend{%s, %.1f%%, $%.2f}", direction, percentChange, absoluteChange);
        }
    }
}
