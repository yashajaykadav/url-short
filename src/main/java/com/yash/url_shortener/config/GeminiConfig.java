package com.yash.url_shortener.config;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash-lite}")
    private String modelName;

    @Value("${gemini.project.id:default}")
    private String projectId;

    @Bean
    public VertexAI vertexAI(){
        return new VertexAI.Builder()
                .setProjectId(projectId)
                .setLocation("us-central1")
                .build();
    }
    @Bean
    public GenerativeModel generativeModel(VertexAI vertexAI) {
        return new GenerativeModel(modelName, vertexAI);
    }
}
