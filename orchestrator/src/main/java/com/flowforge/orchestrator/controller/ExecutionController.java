package com.flowforge.orchestrator.controller;

import com.flowforge.orchestrator.dto.ExecutionResponseDto;
import com.flowforge.orchestrator.entity.ExecutionStatus;
import com.flowforge.orchestrator.entity.WorkflowExecution;
import com.flowforge.orchestrator.repository.WorkflowExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/executions")
@RequiredArgsConstructor
public class ExecutionController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final WorkflowExecutionRepository executionRepository;

    @GetMapping
    public Mono<ResponseEntity<Page<ExecutionResponseDto>>> listExecutions(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @RequestParam(value = "workflowId", required = false) UUID workflowId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort
    ) {
        return Mono.fromCallable(() -> {
                    PageRequest pageRequest = PageRequest.of(page, size, parseSort(sort));
                    Page<WorkflowExecution> result = (workflowId == null)
                            ? executionRepository.findByUserId(userId, pageRequest)
                            : executionRepository.findByUserIdAndWorkflowId(userId, workflowId, pageRequest);
                    return result.map(this::toDto);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{executionId}")
    public Mono<ResponseEntity<ExecutionResponseDto>> getExecution(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID executionId
    ) {
        return Mono.fromCallable(() -> executionRepository.findByIdAndUserId(executionId, userId).orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .map(execution -> execution == null
                        ? ResponseEntity.notFound().build()
                        : ResponseEntity.ok(toDto(execution)));
    }

    @PostMapping("/{executionId}/cancel")
    public Mono<ResponseEntity<ExecutionResponseDto>> cancelExecution(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID executionId
    ) {
        return Mono.fromCallable(() -> executionRepository.findByIdAndUserId(executionId, userId).orElse(null))
                .subscribeOn(Schedulers.boundedElastic())
                .map(execution -> {
                    if (execution == null) {
                        return ResponseEntity.notFound().build();
                    }
                    if (execution.getStatus() == ExecutionStatus.COMPLETED || execution.getStatus() == ExecutionStatus.FAILED) {
                        return ResponseEntity.ok(toDto(execution));
                    }
                    execution.setStatus(ExecutionStatus.CANCELLED);
                    WorkflowExecution saved = executionRepository.save(execution);
                    return ResponseEntity.ok(toDto(saved));
                });
    }

    private ExecutionResponseDto toDto(WorkflowExecution execution) {
        return ExecutionResponseDto.builder()
                .id(execution.getId())
                .workflowId(execution.getWorkflowId())
                .userId(execution.getUserId())
                .status(execution.getStatus())
                .currentStep(execution.getCurrentStep())
                .triggerPayload(execution.getTriggerPayload())
                .stepOutputs(execution.getStepOutputs())
                .createdAt(execution.getCreatedAt())
                .updatedAt(execution.getUpdatedAt())
                .build();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].isBlank() ? "createdAt" : parts[0].trim();
        Sort.Direction direction = (parts.length == 2) ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
