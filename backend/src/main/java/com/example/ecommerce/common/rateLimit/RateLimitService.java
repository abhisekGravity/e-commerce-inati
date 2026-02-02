package com.example.ecommerce.common.rateLimit;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, RequestCounter> requestMap = new ConcurrentHashMap<>();

    private final int MAX_REQUESTS = 10;
    private final long WINDOW_MS = 60_000;

    public boolean allowRequest(String tenantId, String clientIp, String apiPath) {
        String key = tenantId + ":" + clientIp + ":" + apiPath;

        RequestCounter counter = requestMap.computeIfAbsent(key, k -> new RequestCounter());

        synchronized (counter) {
            long now = Instant.now().toEpochMilli();
            if (now - counter.startTime > WINDOW_MS) {
                counter.startTime = now;
                counter.count = 0;
            }

            if (counter.count < MAX_REQUESTS) {
                counter.count++;
                return true;
            } else {
                return false;
            }
        }
    }

    private static class RequestCounter {
        long startTime = Instant.now().toEpochMilli();
        int count = 0;
    }
}