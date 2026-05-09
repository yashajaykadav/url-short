package com.yash.url_shortener.service;

import com.yash.url_shortener.event.ClickEvent;
import com.yash.url_shortener.repository.UrlMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaConsumerService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "click-events", groupId = "url-shortener-group")
    @Transactional
    public void consumeClickEvent(String message) {
        try {
            ClickEvent event = objectMapper.readValue(message, ClickEvent.class);
            System.out.println("📥 Received click event from Kafka for: " + event.getShortCode());

            urlMappingRepository.findByShortCode(event.getShortCode()).ifPresent(mapping -> {
                mapping.incrementClickCount();
                urlMappingRepository.save(mapping);
                System.out.println("✅ Updated click count for: " + event.getShortCode() +
                        " (Total clicks: " + mapping.getClickCount() + ")");
            });
        } catch (Exception e) {
            System.err.println("❌ Failed to process click event: " + e.getMessage());
        }
    }
}