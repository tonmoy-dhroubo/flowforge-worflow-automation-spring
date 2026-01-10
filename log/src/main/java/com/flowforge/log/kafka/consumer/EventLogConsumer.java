package com.flowforge.log.kafka.consumer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowforge.log.service.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component @RequiredArgsConstructor @Slf4j
public class EventLogConsumer {
    private final LoggingService loggingService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"${app.kafka.topics.trigger-events}", "${app.kafka.topics.execution-result}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, Object> record) {
        try {
            Map<String, Object> payload = objectMapper.convertValue(record.value(), new TypeReference<>() {});

            boolean isTriggerEvent = record.topic().contains("trigger");

            UUID userId = coerceUuid(payload.get("userId"));
            if (userId == null) {
                userId = coerceUuid(payload.get("user_id"));
            }

            UUID workflowId = coerceUuid(payload.get("workflowId"));
            UUID executionId = isTriggerEvent ? null : coerceUuid(payload.get("executionId"));
            UUID eventId = isTriggerEvent ? coerceUuid(payload.get("eventId")) : null;

            String type = isTriggerEvent ? "TRIGGER" : "RESULT";
            String status = String.valueOf(payload.getOrDefault("status", isTriggerEvent ? "FIRED" : "INFO"));

            loggingService.logEvent(userId, executionId, eventId, workflowId, type, status, payload);
        } catch (Exception e) { log.error("Log error", e); }
    }

    private UUID coerceUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID uuid) {
            return uuid;
        }
        try {
            return UUID.fromString(String.valueOf(value));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
