package com.flowforge.orchestrator.dto;

import lombok.Data;
import java.util.Map;

@Data
public class TriggerDto {
    private String type;
    private Map<String, Object> config;
}