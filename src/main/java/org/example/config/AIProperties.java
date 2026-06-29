package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "ai")
public class AIProperties {

    private String provider = "gemini";

    private CacheConfig cache = new CacheConfig();
    private RateLimitConfig rateLimit = new RateLimitConfig();
    private FallbackConfig fallback = new FallbackConfig();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public CacheConfig getCache() {
        return cache;
    }

    public void setCache(CacheConfig cache) {
        this.cache = cache;
    }

    public RateLimitConfig getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimitConfig rateLimit) {
        this.rateLimit = rateLimit;
    }

    public FallbackConfig getFallback() {
        return fallback;
    }

    public void setFallback(FallbackConfig fallback) {
        this.fallback = fallback;
    }

    public static class CacheConfig {
        private boolean enabled = true;
        private int ttlDays = 30;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getTtlDays() {
            return ttlDays;
        }

        public void setTtlDays(int ttlDays) {
            this.ttlDays = ttlDays;
        }
    }


    public static class RateLimitConfig {
        private int requestsPerMinute = 60;
        private int monthlyTokenLimit = 1_000_000;

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }

        public int getMonthlyTokenLimit() {
            return monthlyTokenLimit;
        }

        public void setMonthlyTokenLimit(int monthlyTokenLimit) {
            this.monthlyTokenLimit = monthlyTokenLimit;
        }
    }


    public static class FallbackConfig {
        private boolean enabled = true;
        private boolean useTemplateInsights = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isUseTemplateInsights() {
            return useTemplateInsights;
        }

        public void setUseTemplateInsights(boolean useTemplateInsights) {
            this.useTemplateInsights = useTemplateInsights;
        }
    }
}
