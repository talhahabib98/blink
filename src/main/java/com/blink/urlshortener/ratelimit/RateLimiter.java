package com.blink.urlshortener.ratelimit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed-window rate limiter keyed by an arbitrary client identifier (e.g.
 * IP address). Entirely in-memory, consistent with the rest of the
 * service's storage strategy; in a multi-instance deployment this would
 * need to move to a shared store (e.g. Redis).
 */
@Component
public class RateLimiter {

    private static final long WINDOW_MILLIS = 60_000L;

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key, int limitPerWindow) {
        long currentWindow = System.currentTimeMillis() / WINDOW_MILLIS;
        Window window = windows.compute(key, (k, existing) -> {
            if (existing == null || existing.windowId != currentWindow) {
                return new Window(currentWindow);
            }
            return existing;
        });
        return window.count.incrementAndGet() <= limitPerWindow;
    }

    private static final class Window {
        private final long windowId;
        private final AtomicInteger count = new AtomicInteger(0);

        private Window(long windowId) {
            this.windowId = windowId;
        }
    }
}
