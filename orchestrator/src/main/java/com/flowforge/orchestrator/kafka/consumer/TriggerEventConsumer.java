package com.flowforge.orchestrator.kafka.consumer;

import com.flowforge.orchestrator.dto.TriggerEventDto;
import com.flowforge.orchestrator.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TriggerEventConsumer {

    private final OrchestrationService orchestrationService;

    @KafkaListener(topics = "${app.kafka.topics.trigger-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTriggerEvent(TriggerEventDto event) {
        log.info("Received trigger event: eventId={}, workflowId={}", event.getEventId(), event.getWorkflowId());
        try {
            orchestrationService.startWorkflowExecution(event);
        } catch (Exception e) {
            log.error("Error processing trigger event for workflowId {}: {}", event.getWorkflowId(), e.getMessage(), e);
            // Implement dead-letter queue or other error handling logic here
        }
    }
}