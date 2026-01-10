package com.flowforge.trigger;

import com.flowforge.trigger.controller.TriggerManagementController;
import com.flowforge.trigger.controller.WebhookController;
import com.flowforge.trigger.config.SecurityConfig;
import com.flowforge.trigger.dto.TriggerRegistrationDto;
import com.flowforge.trigger.dto.WebhookPayloadDto;
import com.flowforge.trigger.entity.TriggerRegistration;
import com.flowforge.trigger.repository.TriggerRegistrationRepository;
import com.flowforge.trigger.service.TriggerService;
import com.flowforge.trigger.service.WebhookTriggerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {TriggerManagementController.class, WebhookController.class})
@Import(SecurityConfig.class)
class TriggerControllersWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TriggerService triggerService;

    @MockBean
    private TriggerRegistrationRepository triggerRepository;

    @MockBean
    private WebhookTriggerService webhookTriggerService;

    @Test
    void createTriggerReturnsDemoResponse() throws Exception {
        UUID userId = UUID.fromString("33333333-3333-3333-3333-333333333333");
        UUID workflowId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID triggerId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        TriggerRegistrationDto response = TriggerRegistrationDto.builder()
                .id(triggerId)
                .workflowId(workflowId)
                .userId(userId)
                .triggerType("webhook")
                .configuration(Map.of("demo", true))
                .enabled(true)
                .webhookToken("demo-token")
                .webhookUrl("http://localhost:8083/webhook/demo-token")
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        when(triggerService.createTrigger(any(TriggerRegistrationDto.class))).thenReturn(response);

        String body = """
                {
                  "workflowId": "%s",
                  "triggerType": "webhook",
                  "configuration": { "demo": true },
                  "enabled": true
                }
                """.formatted(workflowId);

        mockMvc.perform(post("/api/v1/triggers")
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(triggerId.toString()))
                .andExpect(jsonPath("$.webhookToken").value("demo-token"));
    }

    @Test
    void webhookHitInvokesServices() throws Exception {
        UUID triggerId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        TriggerRegistration trigger = TriggerRegistration.builder()
                .id(triggerId)
                .workflowId(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"))
                .userId(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee"))
                .triggerType("webhook")
                .enabled(true)
                .webhookToken("demo-token")
                .build();

        when(triggerRepository.findByWebhookToken("demo-token")).thenReturn(Optional.of(trigger));
        when(webhookTriggerService.extractHeaders(any())).thenReturn(Map.of("x-demo", "1"));
        when(webhookTriggerService.extractQueryParams(any())).thenReturn(Map.of());

        mockMvc.perform(post("/webhook/demo-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hello\":\"world\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(webhookTriggerService).processWebhookRequest(eq(trigger), any(WebhookPayloadDto.class));
        verify(triggerService).markTriggerFired(triggerId);
    }
}
