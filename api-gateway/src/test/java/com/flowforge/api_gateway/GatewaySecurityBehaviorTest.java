package com.flowforge.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class GatewaySecurityBehaviorTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void protectedRoutesRejectUnauthenticatedRequests() {
        webTestClient.get()
                .uri("/api/v1/workflows")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void webhookRouteIsNotBlockedByAuth() {
        webTestClient.post()
                .uri("/webhook/demo-token")
                .exchange()
                .expectStatus()
                .value(status -> assertThat(status).isNotIn(401, 403));
    }

    @Test
    void authRoutesArePublicEvenWithoutJwt() {
        webTestClient.post()
                .uri("/auth/login")
                .exchange()
                .expectStatus()
                .value(status -> assertThat(status).isNotIn(401, 403));
    }
}
