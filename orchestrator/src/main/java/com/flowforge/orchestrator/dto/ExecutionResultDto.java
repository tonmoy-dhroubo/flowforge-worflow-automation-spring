package com.flowforge.orchestrator.dto;

import lombok.Data;
import java.util.Map;
import java.util.UUID;

@Data
public class ExecutionResultDto {
    private UUID executionId;
    private UUID workflowId;
    private int stepIndex;
    private String status; // e.g., "SUCCESS", "FAILURE"
    private Map<String, Object> output;
    private String errorMessage;
}