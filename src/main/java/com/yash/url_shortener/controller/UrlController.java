package com.yash.url_shortener.controller;

import com.yash.url_shortener.entity.UrlMapping;
import com.yash.url_shortener.event.ClickEvent;
import com.yash.url_shortener.service.KafkaProducerService;
import com.yash.url_shortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.yash.url_shortener.service.AIAnalyticsService;

@RestController
@Tag(name = "URL Shortener", description = "URL shortening and analytics APIs")
public class UrlController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private AIAnalyticsService aiAnalyticsService;

    @PostMapping("/shorten")
    @Operation(summary = "Shorten a URL", description = "Convert a long URL to a short code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL shortened successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL provided")
    })
    public Map<String, String> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("longUrl");
        UrlMapping urlMapping = urlShortenerService.shortenUrl(longUrl);

        Map<String, String> response = new HashMap<>();
        response.put("shortCode", urlMapping.getShortCode());
        response.put("shortUrl", "http://localhost:8080/" + urlMapping.getShortCode());
        response.put("longUrl", urlMapping.getLongUrl());

        return response;
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to original URL" , description = "Redirect short URL to original destination")
    public RedirectView redirect(@PathVariable String shortCode, HttpServletRequest request) {
        return urlShortenerService.getOriginalUrl(shortCode)
                .map(mapping -> {
                    // Send click event to Kafka (async)
                    String userAgent = request.getHeader("User-Agent");
                    String ipAddress = request.getRemoteAddr();
                    ClickEvent clickEvent = new ClickEvent(shortCode, userAgent, ipAddress);
                    kafkaProducerService.sendClickEvent(clickEvent);

                    // Redirect immediately
                    RedirectView redirectView = new RedirectView();
                    redirectView.setUrl(mapping.getLongUrl());
                    return redirectView;
                })
                .orElseGet(() -> {
                    RedirectView redirectView = new RedirectView();
                    redirectView.setUrl("http://localhost:8080/error");
                    return redirectView;
                });
    }

    @GetMapping("/stats/{shortCode}")
    public Map<String, Object> getStats(@PathVariable String shortCode) {
        return urlShortenerService.getOriginalUrl(shortCode)
                .map(mapping -> {
                    Map<String, Object> stats = new HashMap<>();
                    stats.put("shortCode", mapping.getShortCode());
                    stats.put("longUrl", mapping.getLongUrl());
                    stats.put("clickCount", mapping.getClickCount());
                    stats.put("createdAt", mapping.getCreateAt());
                    return stats;
                })
                .orElseThrow(() -> new RuntimeException("URL not found"));
    }
    @GetMapping("/ai/insights/{shortCode}")
    @Operation(summary = "Get AI-powered insights", description = "Analyze URL performance using Google Gemini AI")
    public Map<String, Object> getAIInsights(@PathVariable String shortCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("shortCode", shortCode);
        response.put("analysis", aiAnalyticsService.getUrlInsights(shortCode));
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    @GetMapping("/ai/top-analysis")
    public Map<String, Object> getTopUrlsAnalysis() {
        Map<String, Object> response = new HashMap<>();
        response.put("analysis", aiAnalyticsService.getTopUrlsAnalysis());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}