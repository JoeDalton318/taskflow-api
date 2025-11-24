package com.taskflow.api.controller;

import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tâches", description = "Gestion des tâches")
@SecurityRequirement(name = "bearer-jwt")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {
    
    private final TaskService taskService;
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle tâche")
    public ResponseEntity<TaskResponse> createTask(
        @Valid @RequestBody TaskRequest request,
        Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(taskService.createTask(request, email));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une tâche par ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
    
    @GetMapping
    @Operation(summary = "Lister toutes les tâches avec pagination et filtres")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) Long creatorId,
        @RequestParam(required = false) Long assignedUserId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<TaskResponse> tasks;
        
        if (search != null && !search.isEmpty()) {
            tasks = taskService.searchTasks(search, pageable);
        } else if (assignedUserId != null) {
            tasks = taskService.getTasksByAssignedUser(assignedUserId, pageable);
        } else if (status != null || priority != null || creatorId != null) {
            tasks = taskService.getTasksByFilters(status, priority, creatorId, pageable);
        } else {
            tasks = taskService.getAllTasks(pageable);
        }
        
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Modifier une tâche")
    public ResponseEntity<TaskResponse> updateTask(
        @PathVariable Long id,
        @Valid @RequestBody TaskRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une tâche")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{taskId}/assign/{userId}")
    @Operation(summary = "Assigner un utilisateur à une tâche")
    public ResponseEntity<TaskResponse> assignUserToTask(
        @PathVariable Long taskId,
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(taskService.assignUserToTask(taskId, userId));
    }
    
    @DeleteMapping("/{taskId}/assign/{userId}")
    @Operation(summary = "Retirer un utilisateur d'une tâche")
    public ResponseEntity<TaskResponse> unassignUserFromTask(
        @PathVariable Long taskId,
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(taskService.unassignUserFromTask(taskId, userId));
    }
}
