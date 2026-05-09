package com.yash.url_shortener.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OllamaService {

    @Autowired
    private ObjectMapper objectMapper;

    // Change this to whichever model you have installed!
    private static final String MODEL_NAME = "tinyllama";  // Change this to your model

    // Ollama API endpoint (runs on localhost:11434 by default)
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";

    public String askOllama(String prompt) {
        try {
            // Create HTTP connection
            URL url = new URL(OLLAMA_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Build JSON request for Ollama
            String jsonInputString = String.format(
                    "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                    MODEL_NAME,
                    escapeJson(prompt)
            );

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // Parse JSON response
                    JsonNode jsonResponse = objectMapper.readTree(response.toString());
                    String result = jsonResponse.path("response").asText();

                    // Clean up the response
                    return result.replace("\n", " ").trim();
                }
            } else {
                return "⚠️ Ollama returned error code: " + responseCode;
            }

        } catch (Exception e) {
            return "⚠️ Cannot connect to Ollama. Make sure Ollama GUI app is running.\nError: " + e.getMessage();
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}