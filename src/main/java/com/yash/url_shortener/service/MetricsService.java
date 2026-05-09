package com.yash.url_shortener.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Timer redirectTimer;

    public MetricsService(@Autowired(required = false) MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        if (meterRegistry != null) {
            this.redirectTimer = Timer.builder("url.redirect.duration")
                    .description("Time taken to process redirects")
                    .register(meterRegistry);
        } else {
            this.redirectTimer = null;
        }
    }

    public void recordRedirect(long duration, String shortCode) {
        if (redirectTimer != null) {
            redirectTimer.record(duration, TimeUnit.MILLISECONDS);
            meterRegistry.counter("url.redirect.total", "shortCode", shortCode).increment();
        }
    }

    public void recordCacheHit(String shortCode) {
        if (meterRegistry != null) {
            meterRegistry.counter("cache.hit", "shortCode", shortCode).increment();
        }
    }

    public void recordCacheMiss(String shortCode) {
        if (meterRegistry != null) {
            meterRegistry.counter("cache.miss", "shortCode", shortCode).increment();
        }
    }

    public void recordKafkaEvent(String shortCode) {
        if (meterRegistry != null) {
            meterRegistry.counter("kafka.event.sent", "shortCode", shortCode).increment();
        }
    }
}