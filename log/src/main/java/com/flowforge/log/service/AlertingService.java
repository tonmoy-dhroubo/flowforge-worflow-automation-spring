package com.flowforge.log.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AlertingService {

    public void onFailure(
            UUID userId,
            UUID executionId,
            UUID eventId,
            UUID workflowId,
            String eventType,
            String status,
            Map<String, Object> data
    ) {
        log.warn(
                "Failure event logged: userId={}, workflowId={}, executionId={}, eventId={}, eventType={}, status={}",
                userId, workflowId, executionId, eventId, eventType, status
        );
    }
}

