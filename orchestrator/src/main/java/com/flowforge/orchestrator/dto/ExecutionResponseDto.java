package com.flowforge.orchestrator.dto;

import com.flowforge.orchestrator.entity.ExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ExecutionResponseDto {
    private UUID id;
    private UUID workflowId;
    private UUID userId;
    private ExecutionStatus status;
    private int currentStep;
    private Map<String, Object> triggerPayload;
    private Map<String, Object> stepOutputs;
    private Instant createdAt;
    private Instant updatedAt;
}

