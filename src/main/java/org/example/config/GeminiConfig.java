package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Configuration
@EnableConfigurationProperties(GeminiConfig.GeminiProperties.class)
public class GeminiConfig {

    private static final Logger logger = LoggerFactory.getLogger(GeminiConfig.class);

    @Bean
    public GeminiClientProvider geminiClientProvider(GeminiProperties geminiProperties) {
        if (geminiProperties.getApiKey() == null || geminiProperties.getApiKey().isEmpty()) {
            logger.warn("Gemini API key not configured. AI insights will use fallback templates.");
        }
        return new GeminiClientProvider(geminiProperties);
    }


    @ConfigurationProperties(prefix = "gemini")
    public static class GeminiProperties {
        private String apiKey;
        private String model = "gemini-2.0-flash";
        private int maxTokens = 1500;
        private int timeoutSeconds = 30;
        private double temperature = 0.7;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }
    }
}
