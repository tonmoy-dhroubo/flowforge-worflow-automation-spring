package com.flowforge.orchestrator.repository;

import com.flowforge.orchestrator.entity.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, UUID> {
    Page<WorkflowExecution> findByUserId(UUID userId, Pageable pageable);
    Page<WorkflowExecution> findByUserIdAndWorkflowId(UUID userId, UUID workflowId, Pageable pageable);
    Optional<WorkflowExecution> findByIdAndUserId(UUID id, UUID userId);
}
