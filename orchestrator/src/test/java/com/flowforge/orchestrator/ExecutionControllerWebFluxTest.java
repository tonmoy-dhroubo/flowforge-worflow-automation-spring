package com.flowforge.orchestrator;

import com.flowforge.orchestrator.controller.ExecutionController;
import com.flowforge.orchestrator.entity.ExecutionStatus;
import com.flowforge.orchestrator.entity.WorkflowExecution;
import com.flowforge.orchestrator.repository.WorkflowExecutionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ExecutionController.class)
class ExecutionControllerWebFluxTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WorkflowExecutionRepository executionRepository;

    @Test
    void listExecutionsReturnsDemoPage() {
        UUID userId = UUID.fromString("55555555-5555-5555-5555-555555555555");

        WorkflowExecution e1 = WorkflowExecution.builder()
                .id(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                .workflowId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"))
                .userId(userId)
                .status(ExecutionStatus.PENDING)
                .currentStep(0)
                .triggerPayload(Map.of("demo", true))
                .stepOutputs(Map.of())
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        Page<WorkflowExecution> page = new PageImpl<>(List.of(e1), PageRequest.of(0, 20), 1);
        when(executionRepository.findByUserId(eq(userId), any())).thenReturn(page);

        webTestClient.get()
                .uri("/api/v1/executions?page=0&size=20")
                .header("X-User-Id", userId.toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].status").isEqualTo("PENDING");
    }

    @Test
    void cancelExecutionTransitionsToCancelled() {
        UUID userId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        UUID executionId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        WorkflowExecution running = WorkflowExecution.builder()
                .id(executionId)
                .workflowId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"))
                .userId(userId)
                .status(ExecutionStatus.RUNNING)
                .currentStep(1)
                .triggerPayload(Map.of())
                .stepOutputs(Map.of())
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        when(executionRepository.findByIdAndUserId(executionId, userId)).thenReturn(Optional.of(running));
        when(executionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        webTestClient.post()
                .uri("/api/v1/executions/{id}/cancel", executionId)
                .header("X-User-Id", userId.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("CANCELLED");
    }
}

