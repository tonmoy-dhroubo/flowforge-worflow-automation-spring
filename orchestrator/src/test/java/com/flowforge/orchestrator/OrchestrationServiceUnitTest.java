package com.flowforge.orchestrator;

import com.flowforge.orchestrator.client.WorkflowServiceClient;
import com.flowforge.orchestrator.dto.ExecutionResultDto;
import com.flowforge.orchestrator.entity.ExecutionStatus;
import com.flowforge.orchestrator.entity.WorkflowExecution;
import com.flowforge.orchestrator.kafka.producer.ExecutionStartProducer;
import com.flowforge.orchestrator.repository.WorkflowExecutionRepository;
import com.flowforge.orchestrator.service.OrchestrationService;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrchestrationServiceUnitTest {

    @Test
    void cancelledExecutionIgnoresIncomingResults() {
        WorkflowExecutionRepository repository = mock(WorkflowExecutionRepository.class);
        WorkflowServiceClient workflowServiceClient = mock(WorkflowServiceClient.class);
        ExecutionStartProducer producer = mock(ExecutionStartProducer.class);
        OrchestrationService service = new OrchestrationService(repository, workflowServiceClient, producer);

        UUID executionId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UUID workflowId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID userId = UUID.fromString("55555555-5555-5555-5555-555555555555");

        WorkflowExecution cancelled = WorkflowExecution.builder()
                .id(executionId)
                .workflowId(workflowId)
                .userId(userId)
                .status(ExecutionStatus.CANCELLED)
                .currentStep(0)
                .triggerPayload(Map.of())
                .stepOutputs(Map.of())
                .build();

        when(repository.findById(executionId)).thenReturn(Optional.of(cancelled));

        ExecutionResultDto result = new ExecutionResultDto();
        result.setExecutionId(executionId);
        result.setWorkflowId(workflowId);
        result.setUserId(userId);
        result.setStepIndex(0);
        result.setStatus("SUCCESS");
        result.setOutput(Map.of("ok", true));

        service.continueWorkflowExecution(result);

        verify(repository, times(1)).findById(executionId);
        verify(repository, never()).save(any());
        verifyNoInteractions(workflowServiceClient);
        verifyNoInteractions(producer);
    }
}

