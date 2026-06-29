package org.example.ai.aggregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class TrendCalculator {


    public static double calculatePercentChange(BigDecimal previous, BigDecimal current) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0;
        }

        BigDecimal change = current.subtract(previous);
        BigDecimal percentChange = change.divide(previous, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return percentChange.doubleValue();
    }


    public static String determineTrend(double percentChange) {
        if (percentChange > 5) {
            return "increased";
        } else if (percentChange < -5) {
            return "decreased";
        } else {
            return "stable";
        }
    }


    public static BigDecimal calculateAverage(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = values.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }


    public static BigDecimal calculateStandardDeviation(List<BigDecimal> values) {
        if (values == null || values.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal average = calculateAverage(values);
        BigDecimal sumOfSquares = values.stream()
                .map(val -> val.subtract(average).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal variance = sumOfSquares.divide(
                BigDecimal.valueOf(values.size() - 1),
                4,
                RoundingMode.HALF_UP
        );

        // Calculate square root
        return new BigDecimal(Math.sqrt(variance.doubleValue()))
                .setScale(2, RoundingMode.HALF_UP);
    }


    public static BigDecimal calculatePercentile(List<BigDecimal> values, int percentile) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> sorted = values.stream()
                .sorted()
                .toList();

        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        index = Math.max(0, Math.min(index, sorted.size() - 1));

        return sorted.get(index);
    }


    public static String formatPercentage(double percentage) {
        return String.format("%.1f%%", percentage);
    }


    public static String formatCurrency(BigDecimal amount) {
        return String.format("$%.2f", amount);
    }
}
