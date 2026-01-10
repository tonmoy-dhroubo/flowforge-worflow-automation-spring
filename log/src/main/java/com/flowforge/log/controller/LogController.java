package com.flowforge.log.controller;

import com.flowforge.log.document.ExecutionLog;
import com.flowforge.log.dto.ExecutionLogResponseDto;
import com.flowforge.log.service.ExecutionLogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final ExecutionLogQueryService queryService;

    @GetMapping
    public ResponseEntity<Page<ExecutionLogResponseDto>> search(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @RequestParam(value = "executionId", required = false) UUID executionId,
            @RequestParam(value = "eventId", required = false) UUID eventId,
            @RequestParam(value = "workflowId", required = false) UUID workflowId,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            Pageable pageable
    ) {
        Page<ExecutionLogResponseDto> page = queryService.search(userId, executionId, eventId, workflowId, eventType, status, from, to, pageable)
                .map(this::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<Page<ExecutionLogResponseDto>> getByExecutionId(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID executionId,
            Pageable pageable
    ) {
        Page<ExecutionLogResponseDto> page = queryService.search(userId, executionId, null, null, null, null, null, null, pageable)
                .map(this::toDto);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/workflows/{workflowId}")
    public ResponseEntity<Page<ExecutionLogResponseDto>> getByWorkflowId(
            @RequestHeader(USER_ID_HEADER) UUID userId,
            @PathVariable UUID workflowId,
            Pageable pageable
    ) {
        Page<ExecutionLogResponseDto> page = queryService.search(userId, null, null, workflowId, null, null, null, null, pageable)
                .map(this::toDto);
        return ResponseEntity.ok(page);
    }

    private ExecutionLogResponseDto toDto(ExecutionLog log) {
        return ExecutionLogResponseDto.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .workflowId(log.getWorkflowId())
                .executionId(log.getExecutionId())
                .eventId(log.getEventId())
                .eventType(log.getEventType())
                .status(log.getStatus())
                .data(log.getData())
                .timestamp(log.getTimestamp())
                .build();
    }
}

