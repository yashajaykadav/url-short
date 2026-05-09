package com.yash.url_shortener.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client geminiClient;
    private final String modelName;

    public GeminiService(@Value("${gemini.api.key}") String apiKey,
                         @Value("${gemini.model:gemini-2.5-flash}") String modelName) {
        // Initialize the official Google GenAI Client
        this.geminiClient = Client.builder()
                .apiKey(apiKey)
                .build();
        this.modelName = modelName;
    }

    public String askGemini(String prompt) {
        try {
            GenerateContentResponse response = geminiClient.models.generateContent(
                    modelName,
                    prompt,
                    null // You can pass generation config here if needed
            );

            // Extract and return the text from the response
            return response.text();

        } catch (Exception e) {
            System.err.println("Gemini API Error: " + e.getMessage());
            return "⚠️ Gemini API temporarily unavailable: " + e.getMessage();
        }
    }
}