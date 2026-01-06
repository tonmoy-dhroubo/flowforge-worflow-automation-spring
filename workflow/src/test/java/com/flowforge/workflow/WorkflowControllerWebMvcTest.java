package com.flowforge.workflow;

import com.flowforge.workflow.controller.WorkflowController;
import com.flowforge.workflow.dto.ActionDto;
import com.flowforge.workflow.dto.TriggerDto;
import com.flowforge.workflow.dto.WorkflowRequest;
import com.flowforge.workflow.dto.WorkflowResponse;
import com.flowforge.workflow.dto.WorkflowSummary;
import com.flowforge.workflow.config.SecurityConfig;
import com.flowforge.workflow.exception.WorkflowNotFoundException;
import com.flowforge.workflow.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkflowController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class WorkflowControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkflowService workflowService;

    @Test
    void createAndGetAreScopedByUserHeaderDemoData() throws Exception {
        UUID userA = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID userB = UUID.fromString("22222222-2222-2222-2222-222222222222");
        UUID workflowId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        WorkflowResponse response = new WorkflowResponse(
                workflowId,
                "Demo Workflow",
                userA,
                true,
                new TriggerDto("webhook", Map.of("demo", true)),
                List.of(new ActionDto("SLACK_MESSAGE", Map.of("webhookUrl", "https://example.com", "message", "Hello"))),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(workflowService.createWorkflow(any(WorkflowRequest.class), eq(userA))).thenReturn(response);
        when(workflowService.getWorkflowByIdAndUser(workflowId, userA)).thenReturn(response);
        when(workflowService.getWorkflowByIdAndUser(workflowId, userB)).thenThrow(new WorkflowNotFoundException("not found"));

        String createBody = """
                {
                  "name": "Demo Workflow",
                  "enabled": true,
                  "trigger": { "type": "webhook", "config": { "demo": true } },
                  "actions": [ { "type": "SLACK_MESSAGE", "config": { "webhookUrl": "https://example.com", "message": "Hello" } } ]
                }
                """;

        mockMvc.perform(post("/api/v1/workflows")
                        .header("X-User-Id", userA.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(workflowId.toString()))
                .andExpect(jsonPath("$.userId").value(userA.toString()));

        mockMvc.perform(get("/api/v1/workflows/{id}", workflowId)
                        .header("X-User-Id", userA.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workflowId.toString()));

        mockMvc.perform(get("/api/v1/workflows/{id}", workflowId)
                        .header("X-User-Id", userB.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsDemoSummaries() throws Exception {
        UUID userA = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(workflowService.getWorkflowsForUser(userA)).thenReturn(List.of(
                new WorkflowSummary(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "W1", true, Instant.parse("2026-01-01T00:00:00Z")),
                new WorkflowSummary(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "W2", false, Instant.parse("2026-01-02T00:00:00Z"))
        ));

        mockMvc.perform(get("/api/v1/workflows")
                        .header("X-User-Id", userA.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
