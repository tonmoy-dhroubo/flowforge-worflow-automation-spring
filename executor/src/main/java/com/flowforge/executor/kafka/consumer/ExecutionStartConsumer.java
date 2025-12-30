package com.flowforge.executor.kafka.consumer;
import com.flowforge.executor.dto.ExecutionStartDto;
import com.flowforge.executor.kafka.producer.ExecutionResultProducer;
import com.flowforge.executor.service.ActionExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class ExecutionStartConsumer {
    private final ActionExecutorService executorService;
    private final ExecutionResultProducer producer;

    @KafkaListener(topics = "${app.kafka.topics.execution-start}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ExecutionStartDto startDto) {
        log.info("Consuming start event: {}", startDto.getExecutionId());
        executorService.executeAction(startDto).subscribe(producer::sendExecutionResult);
    }
}