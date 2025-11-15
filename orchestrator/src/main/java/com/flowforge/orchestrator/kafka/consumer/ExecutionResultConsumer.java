package com.flowforge.orchestrator.kafka.consumer;

import com.flowforge.orchestrator.dto.ExecutionResultDto;
import com.flowforge.orchestrator.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionResultConsumer {

    private final OrchestrationService orchestrationService;

    @KafkaListener(topics = "${app.kafka.topics.execution-result}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeExecutionResult(ExecutionResultDto result) {
        log.info("Received execution result: executionId={}, status={}", result.getExecutionId(), result.getStatus());
        try {
            orchestrationService.continueWorkflowExecution(result);
        } catch (Exception e) {
            log.error("Error processing execution result for executionId {}: {}", result.getExecutionId(), e.getMessage(), e);
            // Implement retry or failure handling logic
        }
    }
}