package com.example.postservice.post_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class AIChatService {

    private final WebClient webClient;

    @Value("${openai.api.key:}")
    private String openAiApiKey; // If not set, defaults to empty string

    public AIChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/").build();
    }

    /**
     * Get AI response from OpenAI. If the config is incorrect or missing, the application continues.
     */
    public String getAIResponse(String prompt) {
        // Check if API key is missing
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            // Log a warning and return a fallback message
            System.err.println("OpenAI API key is not configured. Skipping AI response.");
            return "Sorry, AI is currently unavailable. Please try again later.";
        }

        String apiEndpoint = "chat/completions";

        // Build the request body for the OpenAI GPT endpoint
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", prompt)
        });

        try {
            Map<String, Object> response = webClient.post()
                    .uri(apiEndpoint)
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                var choices = (java.util.List<Object>) response.get("choices");
                if (!choices.isEmpty()) {
                    var firstChoice = (Map<String, Object>) choices.get(0);
                    var message = (Map<String, Object>) firstChoice.get("message");
                    if (message != null && message.containsKey("content")) {
                        return (String) message.get("content");
                    }
                }
            }

            // If response is null or doesn't have the expected fields, fallback
            return "I'm sorry, but I couldn't process your request. Please try again later.";

        } catch (Exception e) {
            // Log error but do not crash the application
            System.err.println("Failed to query OpenAI API: " + e.getMessage());
            return "I'm sorry, there was an error handling your request. Please try again later.";
        }
    }
}
