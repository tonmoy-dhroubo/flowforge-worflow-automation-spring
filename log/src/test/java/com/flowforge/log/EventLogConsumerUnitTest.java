package com.flowforge.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowforge.log.kafka.consumer.EventLogConsumer;
import com.flowforge.log.service.LoggingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EventLogConsumerUnitTest {

    @Test
    void triggerTopicMapsToTriggerEventTypeAndUsesEventId() {
        LoggingService loggingService = mock(LoggingService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        EventLogConsumer consumer = new EventLogConsumer(loggingService, objectMapper);

        UUID userId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000000");
        UUID workflowId = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000000");
        UUID eventId = UUID.fromString("cccccccc-0000-0000-0000-000000000000");

        Map<String, Object> payload = Map.of(
                "userId", userId.toString(),
                "workflowId", workflowId.toString(),
                "eventId", eventId.toString(),
                "triggerType", "webhook"
        );

        ConsumerRecord<String, Object> record = new ConsumerRecord<>("trigger.events", 0, 0L, workflowId.toString(), payload);
        consumer.consume(record);

        verify(loggingService, times(1)).logEvent(eq(userId), isNull(), eq(eventId), eq(workflowId), eq("TRIGGER"), eq("FIRED"), anyMap());
    }
}

