package com.flowforge.executor.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ExecutionResultDto {
    private UUID executionId;
    private UUID workflowId;
    private UUID userId;
    private int stepIndex;
    private String status;
    private Map<String, Object> output;
    private String errorMessage;
}
