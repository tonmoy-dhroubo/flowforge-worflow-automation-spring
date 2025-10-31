package com.flowforge.workflow.service;

import com.flowforge.workflow.dto.WorkflowRequestDTO;
import com.flowforge.workflow.dto.WorkflowResponseDTO;
import com.flowforge.workflow.entity.Workflow;
import com.flowforge.workflow.exception.WorkflowNotFoundException;
import com.flowforge.workflow.repository.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;

    @Override
    @Transactional
    public WorkflowResponseDTO createWorkflow(WorkflowRequestDTO request, UUID userId) {
        Workflow workflow = Workflow.builder()
                .name(request.getName())
                .description(request.getDescription())
                .triggerDefinition(request.getTriggerDefinition())
                .actionDefinitions(request.getActionDefinitions())
                .userId(userId)
                .build();
        Workflow savedWorkflow = workflowRepository.save(workflow);
        return mapToResponseDTO(savedWorkflow);
    }

    @Override
    public WorkflowResponseDTO getWorkflowById(UUID id, UUID userId) {
        Workflow workflow = workflowRepository.findById(id)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found"));
        return mapToResponseDTO(workflow);
    }

    @Override
    public List<WorkflowResponseDTO> getWorkflowsByUserId(UUID userId) {
        List<Workflow> workflows = workflowRepository.findByUserId(userId);
        return workflows.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkflowResponseDTO updateWorkflow(UUID id, WorkflowRequestDTO request, UUID userId) {
        Workflow workflow = workflowRepository.findById(id)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found"));
        workflow.setName(request.getName());
        workflow.setDescription(request.getDescription());
        workflow.setTriggerDefinition(request.getTriggerDefinition());
        workflow.setActionDefinitions(request.getActionDefinitions());
        Workflow updatedWorkflow = workflowRepository.save(workflow);
        return mapToResponseDTO(updatedWorkflow);
    }

    @Override
    @Transactional
    public void deleteWorkflow(UUID id, UUID userId) {
        Workflow workflow = workflowRepository.findById(id)
                .filter(w -> w.getUserId().equals(userId))
                .orElseThrow(() -> new WorkflowNotFoundException("Workflow not found"));
        workflowRepository.delete(workflow);
    }

    private WorkflowResponseDTO mapToResponseDTO(Workflow workflow) {
        return WorkflowResponseDTO.builder()
                .id(workflow.getId())
                .name(workflow.getName())
                .description(workflow.getDescription())
                .triggerDefinition(workflow.getTriggerDefinition())
                .actionDefinitions(workflow.getActionDefinitions())
                .userId(workflow.getUserId())
                .createdAt(workflow.getCreatedAt())
                .updatedAt(workflow.getUpdatedAt())
                .build();
    }
}