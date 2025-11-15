package com.flowforge.orchestrator.dto;

import lombok.Data;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
public class TriggerEventDto {
    private UUID eventId;
    private UUID triggerId;
    private UUID workflowId;
    private UUID userId;
    private String triggerType;
    private Instant timestamp;
    private Map<String, Object> payload;
    private Map<String, Object> metadata;
}