package com.flowforge.log.service;
import com.flowforge.log.document.ExecutionLog;
import com.flowforge.log.repository.ExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service @RequiredArgsConstructor @Slf4j
public class LoggingService {
    private final ExecutionLogRepository repository;
    public void logEvent(UUID executionId, UUID workflowId, String eventType, String status, Map<String, Object> data) {
        repository.save(ExecutionLog.builder()
                .executionId(executionId).workflowId(workflowId)
                .eventType(eventType).status(status)
                .data(data).timestamp(Instant.now()).build());
        log.info("Logged event: {}", eventType);
    }
}