package com.blink.urlshortener.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

public class CreateShortUrlRequest {

    @NotBlank(message = "originalUrl must not be blank")
    private String originalUrl;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,32}$", message = "customAlias must be 3-32 characters of letters, digits, '-' or '_'")
    private String customAlias;

    @PositiveOrZero(message = "expiresInDays must not be negative")
    @Max(value = 3650, message = "expiresInDays must not exceed 3650")
    private Long expiresInDays;

    public CreateShortUrlRequest() {
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCustomAlias() {
        return customAlias;
    }

    public void setCustomAlias(String customAlias) {
        this.customAlias = customAlias;
    }

    public Long getExpiresInDays() {
        return expiresInDays;
    }

    public void setExpiresInDays(Long expiresInDays) {
        this.expiresInDays = expiresInDays;
    }
}
