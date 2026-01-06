package com.flowforge.executor;

import com.flowforge.executor.dto.ExecutionStartDto;
import com.flowforge.executor.plugin.ActionPlugin;
import com.flowforge.executor.plugin.PluginManager;
import com.flowforge.executor.service.ActionExecutorService;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ActionExecutorServiceValidationTest {

    @Test
    void missingRequiredSlackConfigProducesFailure() {
        ActionExecutorService service = new ActionExecutorService(new PluginManager(java.util.List.<ActionPlugin>of()));

        ExecutionStartDto startDto = ExecutionStartDto.builder()
                .executionId(UUID.randomUUID())
                .workflowId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .stepIndex(0)
                .actionType("SLACK_MESSAGE")
                .actionConfig(Map.of("message", "hello"))
                .triggerPayload(Map.of("demo", true))
                .context(Map.of())
                .build();

        var result = service.executeAction(startDto).block();
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("FAILURE");
        assertThat(result.getErrorMessage()).contains("webhookUrl");
    }
}
