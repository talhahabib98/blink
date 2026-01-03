package com.blink.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blink")
public class BlinkProperties {

    private String baseUrl = "http://localhost:8080";
    private ShortCode shortCode = new ShortCode();
    private long defaultExpiryDays = 365;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public ShortCode getShortCode() {
        return shortCode;
    }

    public void setShortCode(ShortCode shortCode) {
        this.shortCode = shortCode;
    }

    public long getDefaultExpiryDays() {
        return defaultExpiryDays;
    }

    public void setDefaultExpiryDays(long defaultExpiryDays) {
        this.defaultExpiryDays = defaultExpiryDays;
    }

    public static class ShortCode {
        private int length = 7;

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }
}
