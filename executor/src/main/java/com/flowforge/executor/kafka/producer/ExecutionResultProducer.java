package com.flowforge.executor.kafka.producer;
import com.flowforge.executor.dto.ExecutionResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class ExecutionResultProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${app.kafka.topics.execution-result}") private String topic;

    public void sendExecutionResult(ExecutionResultDto result) {
        log.info("Sending result for execution: {}", result.getExecutionId());
        kafkaTemplate.send(topic, result.getExecutionId().toString(), result);
    }
}