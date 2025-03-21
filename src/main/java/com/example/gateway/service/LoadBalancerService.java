package com.example.gateway.service;

import com.example.gateway.config.InternalServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class LoadBalancerService {

    private static final Logger log = LoggerFactory.getLogger(LoadBalancerService.class);

    private final InternalServiceProperties internalServiceProperties;
    private final StringRedisTemplate redisTemplate;

    public LoadBalancerService(InternalServiceProperties props,
                               StringRedisTemplate redisTemplate) {
        this.internalServiceProperties = props;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Picks the "best key" (url1, url2, url3, etc.) with minimal queue count, increments it,
     * then returns that key. The caller can retrieve the actual URL from the config Map.
     */
    public String acquireUrl() {
        // Sleep 1 second to simulate load, per your existing logic
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }

        // Get the config map => { "url1"->"...", "url2"->"...", ... }
        Map<String, String> urlMap = internalServiceProperties.getUrls();
        if (urlMap.isEmpty()) {
            throw new IllegalStateException("No internal service keys configured!");
        }

        String bestKey = null;
        int bestCount = Integer.MAX_VALUE;

        // We'll iterate over the KEYS (url1, url2, url3, etc.)
        Set<String> allKeys = urlMap.keySet();
        for (String key : allKeys) {
            String countStr = redisTemplate.opsForValue().get(key); // e.g. "url1"
            int count = (countStr != null) ? Integer.parseInt(countStr) : 0;
            if (count < bestCount) {
                bestCount = count;
                bestKey = key;
            }
        }

        // increment the chosen key
        redisTemplate.opsForValue().increment(bestKey);

        log.info("acquireKey() => bestKey={}, newCount={}", bestKey, (bestCount + 1));
        return bestKey;
    }

    /**
     * Decrements the queue count for the given key (like "url1"),
     * then logs the entire set of keys with their queue counts.
     */
    public void releaseUrl(String key) {
        redisTemplate.opsForValue().decrement(key);

        // build a log of the queue sizes
        Map<String, String> urlMap = internalServiceProperties.getUrls();
        StringBuilder sb = new StringBuilder("Current queue sizes: ");
        for (String eachKey : urlMap.keySet()) {
            String countStr = redisTemplate.opsForValue().get(eachKey);
            int c = (countStr != null) ? Integer.parseInt(countStr) : 0;
            sb.append("[").append(eachKey).append("=").append(c).append("] ");
        }

        log.info("releaseKey({}) => Decremented. {}", key, sb.toString());
    }
}
