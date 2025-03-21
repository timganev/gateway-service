package com.example.gateway.service;

import com.example.gateway.config.InternalServiceProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoadBalancerService {

    private final InternalServiceProperties internalServiceProperties;
    private final StringRedisTemplate redisTemplate;

    public LoadBalancerService(InternalServiceProperties props,
                               StringRedisTemplate redisTemplate) {
        this.internalServiceProperties = props;
        this.redisTemplate = redisTemplate;
    }

    public String acquireUrl() {
        //Sleep 1 second to simulate load
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Map<String, String> urlMap = internalServiceProperties.getUrls();
        if (urlMap.isEmpty()) {
            throw new IllegalStateException("No internal service URLs configured in application.properties");
        }

        String bestUrl = null;
        Integer bestCount = Integer.MAX_VALUE;

        for (String url : urlMap.values()) {
            String countStr = redisTemplate.opsForValue().get(url);
            int count = (countStr != null) ? Integer.parseInt(countStr) : 0;

            if (count < bestCount) {
                bestCount = count;
                bestUrl = url;
            }
        }

        //Increment the chosen URL's queue count
        redisTemplate.opsForValue().increment(bestUrl);

        return bestUrl;
    }

    // Decrements the queue count for the given URL in Redis.
    public void releaseUrl(String url) {
        redisTemplate.opsForValue().decrement(url);
    }
}
