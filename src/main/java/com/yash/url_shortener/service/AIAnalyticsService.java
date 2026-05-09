package com.yash.url_shortener.service;

import com.yash.url_shortener.entity.UrlMapping;
import com.yash.url_shortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AIAnalyticsService {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    public String getUrlInsights(String shortCode) {
        return urlMappingRepository.findByShortCode(shortCode)
                .map(mapping -> {
                    int clicks = mapping.getClickCount();
                    LocalDateTime created = mapping.getCreateAt();
                    long daysOld = Math.max(1, ChronoUnit.DAYS.between(created, LocalDateTime.now()));
                    double clicksPerDay = (double) clicks / daysOld;

                    String prompt = String.format("""
                    You are a URL analytics expert. Analyze this URL performance data:
                    
                    URL: %s
                    Total Clicks: %d
                    Clicks per day: %.1f
                    Created: %s
                    
                    Provide a concise analysis (max 150 words) covering:
                    1. Performance assessment
                    2. Engagement prediction for next 7 days
                    3. One actionable optimization tip
                    """,
                            mapping.getLongUrl(), clicks, clicksPerDay, created.toLocalDate().toString()
                    );

                    String aiResponse = geminiService.askGemini(prompt);

                    return String.format(
                            "📊 URL: %s\n📈 Total clicks: %d\n📈 Daily average: %.1f\n📅 Age: %d days\n\n🤖 Analysis:\n%s",
                            shortCode, clicks, clicksPerDay, daysOld, aiResponse
                    );
                })
                .orElse("❌ URL not found: " + shortCode);
    }

    public String getTopUrlsAnalysis() {
        List<UrlMapping> allUrls = urlMappingRepository.findAll();

        if (allUrls.isEmpty()) {
            return "No URLs have been shortened yet. Create some links first!";
        }

        // Get top 5 URLs
        List<UrlMapping> topUrls = allUrls.stream()
                .sorted((a, b) -> b.getClickCount().compareTo(a.getClickCount()))
                .limit(5)
                .toList();

        // Calculate total clicks using stream
        int totalClicks = allUrls.stream()
                .mapToInt(UrlMapping::getClickCount)
                .sum();

        // Build URLs data string
        StringBuilder urlsData = new StringBuilder();
        for (int i = 0; i < topUrls.size(); i++) {
            UrlMapping url = topUrls.get(i);
            double percentage = totalClicks > 0
                    ? (url.getClickCount() * 100.0) / totalClicks
                    : 0;
            urlsData.append(String.format("%d. Clicks: %d (%.1f%%) - %s\n",
                    i + 1, url.getClickCount(), percentage, url.getLongUrl()));
        }

        double topUrlPercentage = totalClicks > 0 && !topUrls.isEmpty()
                ? (topUrls.get(0).getClickCount() * 100.0) / totalClicks
                : 0;

        String prompt = String.format("""
            Analyze these top performing URLs from a link shortener:
            
            %s
            
            Total URLs: %d
            Total clicks: %d
            Top URL share: %.1f%%
            
            Provide: 1) Pattern analysis, 2) Audience insight, 3) One recommendation
            """,
                urlsData.toString(), allUrls.size(), totalClicks, topUrlPercentage
        );

        String aiResponse = geminiService.askGemini(prompt);

        return String.format(
                "🏆 TOP 5 URLs\n%s\n\n📊 Total clicks: %d | URLs: %d\n\n💡 Analysis:\n%s",
                urlsData.toString(), totalClicks, allUrls.size(), aiResponse
        );
    }
}