package com.flowforge.workflow.service;

import com.flowforge.workflow.dto.WorkflowRequestDTO;
import com.flowforge.workflow.dto.WorkflowResponseDTO;

import java.util.List;
import java.util.UUID;

public interface WorkflowService {

    WorkflowResponseDTO createWorkflow(WorkflowRequestDTO request, UUID userId);

    WorkflowResponseDTO getWorkflowById(UUID id, UUID userId);

    List<WorkflowResponseDTO> getWorkflowsByUserId(UUID userId);

    WorkflowResponseDTO updateWorkflow(UUID id, WorkflowRequestDTO request, UUID userId);

    void deleteWorkflow(UUID id, UUID userId);
}