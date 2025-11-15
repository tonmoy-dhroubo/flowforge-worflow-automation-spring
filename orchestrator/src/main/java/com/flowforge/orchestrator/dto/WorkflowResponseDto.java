package com.flowforge.orchestrator.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class WorkflowResponseDto {
    private UUID id;
    private String name;
    private UUID userId;
    private boolean enabled;
    private TriggerDto trigger;
    private List<ActionDto> actions;
    private Instant createdAt;
    private Instant updatedAt;
}