package com.flowforge.executor.service;
import com.flowforge.executor.dto.ExecutionResultDto;
import com.flowforge.executor.dto.ExecutionStartDto;
import com.flowforge.executor.plugin.PluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service @RequiredArgsConstructor @Slf4j
public class ActionExecutorService {
    private final PluginManager pluginManager;
    public Mono<ExecutionResultDto> executeAction(ExecutionStartDto startDto) {
        return Mono.justOrEmpty(pluginManager.getPlugin(startDto.getActionType()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Unknown action: " + startDto.getActionType())))
                .flatMap(plugin -> {
                    try { return plugin.execute(startDto.getActionConfig(), startDto.getTriggerPayload()); }
                    catch (Exception e) { return Mono.error(e); }
                })
                .map(output -> ExecutionResultDto.builder()
                        .executionId(startDto.getExecutionId())
                        .workflowId(startDto.getWorkflowId())
                        .stepIndex(startDto.getStepIndex())
                        .status("SUCCESS")
                        .output(output).build())
                .onErrorResume(e -> {
                    log.error("Execution failed", e);
                    return Mono.just(ExecutionResultDto.builder()
                            .executionId(startDto.getExecutionId())
                            .workflowId(startDto.getWorkflowId())
                            .stepIndex(startDto.getStepIndex())
                            .status("FAILURE")
                            .errorMessage(e.getMessage()).build());
                });
    }
}