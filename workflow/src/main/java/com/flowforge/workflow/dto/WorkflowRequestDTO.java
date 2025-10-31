package com.flowforge.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private Map<String, Object> triggerDefinition;

    private List<Map<String, Object>> actionDefinitions;
}