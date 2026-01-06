package com.flowforge.log;

import com.flowforge.log.controller.LogController;
import com.flowforge.log.document.ExecutionLog;
import com.flowforge.log.service.ExecutionLogQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LogController.class)
class LogControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExecutionLogQueryService queryService;

    @Test
    void searchReturnsPagedDemoLogs() throws Exception {
        UUID userId = UUID.fromString("88888888-8888-8888-8888-888888888888");
        UUID workflowId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        UUID executionId = UUID.fromString("12121212-1212-1212-1212-121212121212");

        ExecutionLog log1 = ExecutionLog.builder()
                .id("log-1")
                .userId(userId)
                .workflowId(workflowId)
                .executionId(executionId)
                .eventType("RESULT")
                .status("SUCCESS")
                .data(Map.of("stepIndex", 0))
                .timestamp(Instant.parse("2026-01-01T00:00:00Z"))
                .build();

        Page<ExecutionLog> page = new PageImpl<>(List.of(log1), PageRequest.of(0, 20), 1);

        when(queryService.search(eq(userId), eq(executionId), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/logs/executions/{executionId}", executionId)
                        .header("X-User-Id", userId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("log-1"))
                .andExpect(jsonPath("$.content[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].workflowId").value(workflowId.toString()))
                .andExpect(jsonPath("$.content[0].executionId").value(executionId.toString()))
                .andExpect(jsonPath("$.content[0].eventType").value("RESULT"))
                .andExpect(jsonPath("$.content[0].status").value("SUCCESS"));
    }
}

