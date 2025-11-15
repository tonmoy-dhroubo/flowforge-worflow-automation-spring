package com.flowforge.orchestrator.kafka.producer;

import com.flowforge.orchestrator.dto.ExecutionStartDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionStartProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.execution-start}")
    private String executionStartTopic;

    public void sendExecutionStartEvent(ExecutionStartDto event) {
        log.info("Sending execution start event: executionId={}, actionType={}", event.getExecutionId(), event.getActionType());
        kafkaTemplate.send(executionStartTopic, event.getExecutionId().toString(), event);
    }
}