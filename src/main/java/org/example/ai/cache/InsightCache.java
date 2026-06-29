package org.example.ai.cache;

import org.example.dto.AIInsightResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class InsightCache {

    private static final Logger logger = LoggerFactory.getLogger(InsightCache.class);

    private final Map<String, CachedInsight> cache = new ConcurrentHashMap<>();


    private String buildKey(Long userId, YearMonth month) {
        return userId + "_" + month.toString();
    }


    public Optional<AIInsightResponse> get(Long userId, YearMonth month) {
        String key = buildKey(userId, month);
        CachedInsight cached = cache.get(key);

        if (cached == null) {
            logger.debug("Cache miss for key: {}", key);
            return Optional.empty();
        }

        // Check if cache is expired (30 days TTL)
        if (cached.isExpired()) {
            logger.debug("Cache expired for key: {}", key);
            cache.remove(key);
            return Optional.empty();
        }

        logger.debug("Cache hit for key: {}", key);
        return Optional.of(cached.insight);
    }


    public void put(Long userId, YearMonth month, AIInsightResponse insight) {
        String key = buildKey(userId, month);
        CachedInsight cached = new CachedInsight(insight, LocalDateTime.now());
        cache.put(key, cached);
        logger.debug("Cached insight for key: {}", key);
    }


    public void invalidate(Long userId, YearMonth month) {
        String key = buildKey(userId, month);
        cache.remove(key);
        logger.debug("Invalidated cache for key: {}", key);
    }


    public void invalidateUser(Long userId) {
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(userId + "_"));
        logger.debug("Invalidated all cache for user: {}", userId);
    }


    public void clear() {
        cache.clear();
        logger.info("Cache cleared");
    }


    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEntries", cache.size());
        stats.put("entries", cache.keySet());
        return stats;
    }


    private static class CachedInsight {
        private final AIInsightResponse insight;
        private final LocalDateTime cachedAt;
        private static final int TTL_HOURS = 24; // Cache for 24 hours

        CachedInsight(AIInsightResponse insight, LocalDateTime cachedAt) {
            this.insight = insight;
            this.cachedAt = cachedAt;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(cachedAt.plusHours(TTL_HOURS));
        }
    }
}