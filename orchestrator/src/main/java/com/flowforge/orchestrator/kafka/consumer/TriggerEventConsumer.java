package com.flowforge.orchestrator.kafka.consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowforge.orchestrator.dto.TriggerEventDto;
import com.flowforge.orchestrator.service.OrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class TriggerEventConsumer {
    private final OrchestrationService orchestrationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.trigger-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTriggerEvent(String message) {
        try {
            TriggerEventDto event = objectMapper.readValue(message, TriggerEventDto.class);
            log.info("Received trigger: {}", event.getEventId());
            orchestrationService.startWorkflowExecution(event);
        } catch (Exception e) {
            log.error("Error consuming trigger", e);
        }
    }
}