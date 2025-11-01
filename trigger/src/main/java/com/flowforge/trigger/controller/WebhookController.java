package com.flowforge.trigger.controller;

import com.flowforge.trigger.dto.TriggerEvent;
import com.flowforge.trigger.dto.WebhookRequest;
import com.flowforge.trigger.dto.WebhookResponse;
import com.flowforge.trigger.service.TriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final TriggerService triggerService;

    /**
     * Generic webhook endpoint that accepts any event
     */
    @PostMapping("/{workflowId}")
    public ResponseEntity<WebhookResponse> handleWebhook(
            @PathVariable String workflowId,
            @RequestBody(required = false) Map<String, Object> payload,
            HttpServletRequest request) {
        
        log.info("Received webhook for workflow: {}", workflowId);

        // Extract headers
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator()
            .forEachRemaining(name -> headers.put(name, request.getHeader(name)));

        // Create trigger event
        String eventId = UUID.randomUUID().toString();
        TriggerEvent event = TriggerEvent.builder()
                .eventId(eventId)
                .triggerType("webhook")
                .payload(payload != null ? payload : new HashMap<>())
                .metadata(Map.of(
                    "workflowId", workflowId,
                    "method", request.getMethod(),
                    "remoteAddr", request.getRemoteAddr(),
                    "contentType", request.getContentType() != null ? request.getContentType() : "unknown"
                ))
                .timestamp(Instant.now())
                .build();

        // Send to Kafka
        triggerService.processTrigger(event);

        WebhookResponse response = WebhookResponse.builder()
                .eventId(eventId)
                .status("accepted")
                .message("Webhook received and queued for processing")
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Named event webhook endpoint
     */
    @PostMapping("/events/{eventName}")
    public ResponseEntity<WebhookResponse> handleNamedEvent(
            @PathVariable String eventName,
            @RequestBody WebhookRequest webhookRequest) {
        
        log.info("Received named event: {}", eventName);

        String eventId = UUID.randomUUID().toString();
        TriggerEvent event = TriggerEvent.builder()
                .eventId(eventId)
                .triggerType("webhook." + eventName)
                .payload(webhookRequest.getData() != null ? webhookRequest.getData() : new HashMap<>())
                .metadata(Map.of(
                    "eventName", eventName,
                    "requestEvent", webhookRequest.getEvent() != null ? webhookRequest.getEvent() : "unknown"
                ))
                .timestamp(Instant.now())
                .build();

        triggerService.processTrigger(event);

        WebhookResponse response = WebhookResponse.builder()
                .eventId(eventId)
                .status("accepted")
                .message("Event received and queued for processing")
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "trigger-service",
            "timestamp", Instant.now().toString()
        ));
    }

    /**
     * Test webhook endpoint (for testing purposes)
     */
    @PostMapping("/test")
    public ResponseEntity<WebhookResponse> testWebhook(@RequestBody Map<String, Object> payload) {
        log.info("Received test webhook");

        String eventId = UUID.randomUUID().toString();
        TriggerEvent event = TriggerEvent.builder()
                .eventId(eventId)
                .triggerType("webhook.test")
                .payload(payload)
                .metadata(Map.of("test", "true"))
                .timestamp(Instant.now())
                .build();

        triggerService.processTrigger(event);

        WebhookResponse response = WebhookResponse.builder()
                .eventId(eventId)
                .status("accepted")
                .message("Test webhook processed successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}