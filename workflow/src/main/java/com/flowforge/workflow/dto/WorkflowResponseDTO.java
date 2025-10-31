package com.flowforge.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private Map<String, Object> triggerDefinition;
    private List<Map<String, Object>> actionDefinitions;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
}