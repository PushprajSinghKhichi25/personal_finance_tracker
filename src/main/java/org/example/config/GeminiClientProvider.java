package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class GeminiClientProvider {

    private static final Logger logger = LoggerFactory.getLogger(GeminiClientProvider.class);
    private static final String GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models";

    private final GeminiConfig.GeminiProperties geminiProperties;
    private volatile boolean isInitialized = false;
    private WebClient webClient;
    private ObjectMapper objectMapper;

    public GeminiClientProvider(GeminiConfig.GeminiProperties geminiProperties) {
        this.geminiProperties = geminiProperties;
        this.objectMapper = new ObjectMapper();
        initialize();
    }


    private void initialize() {
        try {
            if (geminiProperties.getApiKey() == null || geminiProperties.getApiKey().isEmpty()) {
                logger.warn("Gemini API key not found. Set GOOGLE_API_KEY environment variable.");
                isInitialized = false;
                return;
            }

            logger.info("Initializing Gemini API client with model: {}", geminiProperties.getModel());
            this.webClient = WebClient.create(GEMINI_API_BASE);
            isInitialized = true;
            logger.info("Gemini API client initialized successfully");

        } catch (Exception e) {
            logger.error("Failed to initialize Gemini API client", e);
            isInitialized = false;
        }
    }


    public boolean isReady() {
        return isInitialized && geminiProperties.getApiKey() != null && !geminiProperties.getApiKey().isEmpty();
    }


    public String getApiKey() {
        return geminiProperties.getApiKey();
    }


    public String getModel() {
        return geminiProperties.getModel();
    }


    public int getMaxTokens() {
        return geminiProperties.getMaxTokens();
    }


    public int getTimeoutSeconds() {
        return geminiProperties.getTimeoutSeconds();
    }


    public double getTemperature() {
        return geminiProperties.getTemperature();
    }


    public WebClient getWebClient() {
        if (!isReady()) {
            throw new IllegalStateException("Gemini API not initialized. Check API key configuration.");
        }
        return webClient;
    }


    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }


    public String getApiEndpoint() {
        return GEMINI_API_BASE + "/" + geminiProperties.getModel() + ":generateContent";
    }
}