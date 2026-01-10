package com.flowforge.executor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ExecutorControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void listsSupportedPlugins() {
        webTestClient.get()
                .uri("/api/v1/executor/plugins")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.supportedActionTypes").isArray()
                .jsonPath("$.supportedActionTypes[?(@ == 'SLACK_MESSAGE')]").exists()
                .jsonPath("$.supportedActionTypes[?(@ == 'GOOGLE_SHEET_ROW')]").exists();
    }
}

