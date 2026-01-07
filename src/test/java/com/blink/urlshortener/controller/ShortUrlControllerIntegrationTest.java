package com.blink.urlshortener.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "blink.rate-limit.enabled=false")
@AutoConfigureMockMvc
class ShortUrlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createThenRedirectThenStatsThenDelete() throws Exception {
        String createPayload = "{\"originalUrl\":\"https://www.example.com/page\"}";

        String responseJson = mockMvc.perform(post("/api/v1/urls")
                        .contentType("application/json")
                        .content(createPayload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.code").exists())
                .andReturn().getResponse().getContentAsString();

        String code = responseJson.split("\"code\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/" + code))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.example.com/page"));

        mockMvc.perform(get("/api/v1/urls/" + code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clickCount").value(1))
                .andExpect(jsonPath("$.expired").value(false));

        mockMvc.perform(delete("/api/v1/urls/" + code))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/urls/" + code))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithCustomAliasThenRejectsDuplicate() throws Exception {
        String payload = "{\"originalUrl\":\"https://example.org\",\"customAlias\":\"integration-alias\"}";

        mockMvc.perform(post("/api/v1/urls").contentType("application/json").content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("integration-alias"));

        mockMvc.perform(post("/api/v1/urls").contentType("application/json").content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already in use")));
    }

    @Test
    void rejectsBlankOriginalUrl() throws Exception {
        mockMvc.perform(post("/api/v1/urls")
                        .contentType("application/json")
                        .content("{\"originalUrl\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value(containsString("originalUrl")));
    }

    @Test
    void redirectReturnsNotFoundForUnknownCode() throws Exception {
        mockMvc.perform(get("/does-not-exist"))
                .andExpect(status().isNotFound());
    }
}
