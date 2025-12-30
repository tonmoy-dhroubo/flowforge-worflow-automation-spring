package com.flowforge.orchestrator.kafka.consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowforge.orchestrator.dto.ExecutionResultDto;
import com.flowforge.orchestrator.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class ExecutionResultConsumer {
    private final OrchestrationService orchestrationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.execution-result}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeExecutionResult(String message) {
        try {
            ExecutionResultDto result = objectMapper.readValue(message, ExecutionResultDto.class);
            log.info("Received result: {}", result.getExecutionId());
            orchestrationService.continueWorkflowExecution(result);
        } catch (Exception e) {
            log.error("Error consuming result", e);
        }
    }
}