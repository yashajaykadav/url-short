package com.yash.url_shortener.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ClickEvent {
    // Getters and Setters
    private String shortCode;
    private String userAgent;
    private String ipAddress;
    private LocalDateTime timestamp;

    public ClickEvent() {}

    public ClickEvent(String shortCode, String userAgent, String ipAddress) {
        this.shortCode = shortCode;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
        this.timestamp = LocalDateTime.now();
    }

}