package com.yash.url_shortener.service;

import com.yash.url_shortener.entity.UrlMapping;
import com.yash.url_shortener.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private Random random = new Random();

    // Generate short code and save URL
    public UrlMapping shortenUrl(String longUrl) {
        String shortCode = generateUniqueShortCode();
        UrlMapping urlMapping = new UrlMapping(shortCode, longUrl);

        UrlMapping saved = urlMappingRepository.save(urlMapping);
        redisCacheService.cacheUrl(shortCode,longUrl);
        return saved;
    }

    // Get original URL by short code
    public Optional<UrlMapping> getOriginalUrl(String shortCode) {
        Optional<String> cachedUrl = redisCacheService.getCachedUrl(shortCode);

        if(cachedUrl.isPresent()){
            UrlMapping mapping = new UrlMapping(shortCode,cachedUrl.get());
            return Optional.of(mapping);
        }
        System.out.println("Fetching from Database for :  "+shortCode);
        Optional<UrlMapping>mapping = urlMappingRepository.findByShortCode(shortCode);

        mapping.ifPresent(m->redisCacheService.cacheUrl(shortCode,m.getLongUrl()));
        return mapping;
    }

    // Update click count when someone visits
    public void recordClick(String shortCode) {
        urlMappingRepository.findByShortCode(shortCode).ifPresent(mapping -> {
            mapping.incrementClickCount();
            urlMappingRepository.save(mapping);
        });
    }

    // Generate unique short code
    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlMappingRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}