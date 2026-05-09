package com.yash.url_shortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    private static final Duration CACHE_TTL = Duration.ofHours(24);

    public void cacheUrl(String shortCode , String longUrl){

        redisTemplate.opsForValue().set(shortCode,longUrl,CACHE_TTL);
        System.out.println("Cached: " +shortCode+" -> "+longUrl);
    }

    public Optional<String>getCachedUrl(String shortCode){
        String longUrl = redisTemplate.opsForValue().get(shortCode);
        if(longUrl!=null){
            System.out.println("Cache Hit "+shortCode);
            return Optional.of(longUrl);
        }
        System.out.println("Cache Missed! for "+shortCode);
        return Optional.empty();
    }

    public void removeFromCache(String shortCode){
       redisTemplate.delete(shortCode);
        System.out.println("removed From Cache: "+shortCode);
    }

    public boolean existInCache(String shortCode){
        Boolean exists = redisTemplate.hasKey(shortCode);
        return exists != null && exists;
    }
}
