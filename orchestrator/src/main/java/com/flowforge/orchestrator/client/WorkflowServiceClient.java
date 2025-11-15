package com.flowforge.orchestrator.client;

import com.flowforge.orchestrator.dto.WorkflowResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceClient {

    private final WebClient workflowWebClient;

    public Mono<WorkflowResponseDto> getWorkflowById(UUID workflowId, UUID userId) {
        log.info("Fetching workflow definition for workflowId: {} and userId: {}", workflowId, userId);
        return workflowWebClient.get()
                .uri("/workflows/{id}", workflowId)
                .header("X-User-Id", userId.toString())
                .retrieve()
                .bodyToMono(WorkflowResponseDto.class)
                .doOnError(error -> log.error("Failed to fetch workflow definition for {}: {}", workflowId, error.getMessage()))
                .doOnSuccess(workflow -> log.info("Successfully fetched workflow: {}", workflow.getName()));
    }
}