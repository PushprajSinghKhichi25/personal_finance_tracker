package org.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class RateLimitManager {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitManager.class);

    private final AIProperties aiProperties;

    private final AtomicInteger requestsThisMinute = new AtomicInteger(0);
    private final AtomicLong tokensUsedThisMonth = new AtomicLong(0);
    private final AtomicInteger estimatedTokensPerRequest = new AtomicInteger(2500);

    private LocalDateTime minuteResetTime = LocalDateTime.now();
    private YearMonth tokenCountMonth = YearMonth.now();

    public RateLimitManager(AIProperties aiProperties) {
        this.aiProperties = aiProperties;
    }


    public synchronized boolean canMakeRequest() {
        resetMinuteCounterIfNeeded();
        resetMonthCounterIfNeeded();

        int maxRequests = aiProperties.getRateLimit().getRequestsPerMinute();
        if (requestsThisMinute.get() >= maxRequests) {
            logger.warn("Rate limit exceeded: {} requests this minute (max: {})",
                    requestsThisMinute.get(), maxRequests);
            return false;
        }

        long estimatedTokens = estimatedTokensPerRequest.get();
        long maxTokens = aiProperties.getRateLimit().getMonthlyTokenLimit();
        if (tokensUsedThisMonth.get() + estimatedTokens > maxTokens) {
            logger.warn("Monthly token limit will be exceeded: {} tokens used + {} estimated (max: {})",
                    tokensUsedThisMonth.get(), estimatedTokens, maxTokens);
            return false;
        }

        return true;
    }


    public synchronized void recordRequest(int inputTokens, int outputTokens) {
        int totalTokens = inputTokens + outputTokens;
        requestsThisMinute.incrementAndGet();
        tokensUsedThisMonth.addAndGet(totalTokens);

        // Update estimated tokens for future requests
        int avg = (estimatedTokensPerRequest.get() + totalTokens) / 2;
        estimatedTokensPerRequest.set(avg);

        long monthlyLimit = aiProperties.getRateLimit().getMonthlyTokenLimit();
        double percentUsed = (tokensUsedThisMonth.get() * 100.0) / monthlyLimit;

        logger.debug("API request recorded. Tokens: {} (total this month: {}, {:.1f}%)",
                totalTokens, tokensUsedThisMonth.get(), percentUsed);

        if (percentUsed > 90) {
            logger.warn("Approaching monthly token limit: {:.1f}% used", percentUsed);
        }
    }


    public synchronized void recordFailedRequest() {
        requestsThisMinute.incrementAndGet();
        logger.debug("Failed API request recorded (no tokens consumed)");
    }


    public synchronized TokenUsageStats getUsageStats() {
        return new TokenUsageStats(
                requestsThisMinute.get(),
                tokensUsedThisMonth.get(),
                estimatedTokensPerRequest.get(),
                aiProperties.getRateLimit().getRequestsPerMinute(),
                aiProperties.getRateLimit().getMonthlyTokenLimit()
        );
    }


    private void resetMinuteCounterIfNeeded() {
        if (LocalDateTime.now().isAfter(minuteResetTime.plusMinutes(1))) {
            int previousCount = requestsThisMinute.getAndSet(0);
            minuteResetTime = LocalDateTime.now();
            logger.debug("Minute counter reset. Previous minute had {} requests", previousCount);
        }
    }


    private void resetMonthCounterIfNeeded() {
        YearMonth currentMonth = YearMonth.now();
        if (!currentMonth.equals(tokenCountMonth)) {
            long previousTotal = tokensUsedThisMonth.getAndSet(0);
            tokenCountMonth = currentMonth;
            logger.info("Monthly token counter reset. Previous month used {} tokens", previousTotal);
        }
    }


    public static class TokenUsageStats {
        public final int requestsThisMinute;
        public final long tokensUsedThisMonth;
        public final int estimatedTokensPerRequest;
        public final int maxRequestsPerMinute;
        public final int maxTokensPerMonth;

        public TokenUsageStats(int requestsThisMinute, long tokensUsedThisMonth,
                               int estimatedTokensPerRequest, int maxRequestsPerMinute, int maxTokensPerMonth) {
            this.requestsThisMinute = requestsThisMinute;
            this.tokensUsedThisMonth = tokensUsedThisMonth;
            this.estimatedTokensPerRequest = estimatedTokensPerRequest;
            this.maxRequestsPerMinute = maxRequestsPerMinute;
            this.maxTokensPerMonth = maxTokensPerMonth;
        }

        public double getTokenUsagePercentage() {
            return (tokensUsedThisMonth * 100.0) / maxTokensPerMonth;
        }

        public double getRequestUsagePercentage() {
            return (requestsThisMinute * 100.0) / maxRequestsPerMinute;
        }

        @Override
        public String toString() {
            return String.format("TokenUsageStats{requests: %d/%d (%.1f%%), tokens: %d/%d (%.1f%%)}",
                    requestsThisMinute, maxRequestsPerMinute, getRequestUsagePercentage(),
                    tokensUsedThisMonth, maxTokensPerMonth, getTokenUsagePercentage());
        }
    }
}