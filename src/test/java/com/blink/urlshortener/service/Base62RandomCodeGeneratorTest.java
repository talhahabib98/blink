package com.blink.urlshortener.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class Base62RandomCodeGeneratorTest {

    private final Base62RandomCodeGenerator generator = new Base62RandomCodeGenerator();

    @Test
    void generatesCodeOfRequestedLength() {
        String code = generator.generate(7);
        assertThat(code).hasSize(7);
    }

    @Test
    void generatesOnlyAlphanumericCharacters() {
        String code = generator.generate(20);
        assertThat(code).matches("^[0-9a-zA-Z]+$");
    }

    @Test
    void generatesDistinctCodesAcrossManyInvocations() {
        Set<String> codes = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            codes.add(generator.generate(10));
        }
        assertThat(codes).hasSize(1000);
    }
}
