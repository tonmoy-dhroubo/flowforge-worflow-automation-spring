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
            UUID wfId = UUID.fromString((String)payload.get("workflowId"));
            UUID exId = payload.containsKey("executionId") ? UUID.fromString((String)payload.get("executionId")) : UUID.fromString((String)payload.get("eventId"));
            String type = record.topic().contains("trigger") ? "TRIGGER" : "RESULT";
            String status = (String) payload.getOrDefault("status", "INFO");
            loggingService.logEvent(exId, wfId, type, status, payload);
        } catch (Exception e) { log.error("Log error", e); }
    }
}