package com.blink.urlshortener.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Produces random Base62 codes (0-9, a-z, A-Z). Using a cryptographically
 * strong RNG rather than a sequential counter avoids leaking the total
 * number of links created and makes codes unguessable.
 */
@Component
public class Base62RandomCodeGenerator implements ShortCodeGenerator {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
