package com.yash.url_shortener.service;

import com.yash.url_shortener.event.ClickEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOPIC = "click-events";

    public void sendClickEvent(ClickEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getShortCode(), jsonMessage);
            System.out.println("📤 Sent click event to Kafka for: " + event.getShortCode());
        } catch (Exception e) {
            System.err.println("❌ Failed to send click event: " + e.getMessage());
        }
    }
}