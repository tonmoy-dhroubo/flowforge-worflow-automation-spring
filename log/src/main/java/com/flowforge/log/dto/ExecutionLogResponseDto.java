package com.flowforge.log.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ExecutionLogResponseDto {
    private String id;
    private UUID userId;
    private UUID workflowId;
    private UUID executionId;
    private UUID eventId;
    private String eventType;
    private String status;
    private Map<String, Object> data;
    private Instant timestamp;
}

