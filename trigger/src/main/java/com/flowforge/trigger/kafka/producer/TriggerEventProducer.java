package com.flowforge.trigger.kafka.producer;

import com.flowforge.trigger.dto.TriggerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class TriggerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.trigger-events}")
    private String triggerEventsTopic;

    public void sendTriggerEvent(TriggerEvent event) {
        log.info("Sending trigger event: {} to topic: {}", event.getEventId(), triggerEventsTopic);
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(triggerEventsTopic, event.getEventId(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent trigger event: {} with offset: {}", 
                    event.getEventId(), 
                    result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send trigger event: {}", event.getEventId(), ex);
            }
        });
    }

    public void sendTriggerEventSync(TriggerEvent event) {
        try {
            log.info("Sending trigger event synchronously: {} to topic: {}", 
                event.getEventId(), triggerEventsTopic);
            
            SendResult<String, Object> result = 
                kafkaTemplate.send(triggerEventsTopic, event.getEventId(), event).get();
            
            log.info("Successfully sent trigger event: {} with offset: {}", 
                event.getEventId(), 
                result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Failed to send trigger event: {}", event.getEventId(), e);
            throw new RuntimeException("Failed to send trigger event", e);
        }
    }
}