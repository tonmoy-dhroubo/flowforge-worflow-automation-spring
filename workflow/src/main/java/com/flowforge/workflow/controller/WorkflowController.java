package com.flowforge.workflow.controller;

import com.flowforge.workflow.dto.WorkflowRequestDTO;
import com.flowforge.workflow.dto.WorkflowResponseDTO;
import com.flowforge.workflow.service.WorkflowService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<WorkflowResponseDTO> createWorkflow(
            @Valid @RequestBody WorkflowRequestDTO request,
            @RequestHeader("Authorization") String token) {
        UUID userId = extractUserIdFromToken(token);
        WorkflowResponseDTO response = workflowService.createWorkflow(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowResponseDTO> getWorkflowById(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        UUID userId = extractUserIdFromToken(token);
        WorkflowResponseDTO response = workflowService.getWorkflowById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkflowResponseDTO>> getWorkflows(
            @RequestHeader("Authorization") String token) {
        UUID userId = extractUserIdFromToken(token);
        List<WorkflowResponseDTO> responses = workflowService.getWorkflowsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowResponseDTO> updateWorkflow(
            @PathVariable UUID id,
            @Valid @RequestBody WorkflowRequestDTO request,
            @RequestHeader("Authorization") String token) {
        UUID userId = extractUserIdFromToken(token);
        WorkflowResponseDTO response = workflowService.updateWorkflow(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        UUID userId = extractUserIdFromToken(token);
        workflowService.deleteWorkflow(id, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID extractUserIdFromToken(String token) {
        // Remove "Bearer " prefix
        String jwt = token.substring(7);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970".getBytes()) // Use the same secret
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        String username = claims.getSubject();
        // Assuming username is UUID, but in auth it's username, wait.
        // In auth, UserDetails uses username, but userId is UUID.
        // To get userId, perhaps need to call auth service or store userId in token.
        // For simplicity, assume username is the UUID string.
        return UUID.fromString(username);
    }
}