package com.taskflow.api.service;

import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.dto.UserResponse;
import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.User;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public TaskResponse createTask(TaskRequest request, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
            .orElseThrow(() -> new RuntimeException("Créateur introuvable"));
        
        Task task = Task.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .status(parseStatus(request.getStatus()))
            .priority(parsePriority(request.getPriority()))
            .dueDate(request.getDueDate())
            .creator(creator)
            .build();
        
        if (request.getAssignedUserIds() != null && !request.getAssignedUserIds().isEmpty()) {
            Set<User> assignedUsers = new HashSet<>(userRepository.findAllById(request.getAssignedUserIds()));
            task.setAssignedUsers(assignedUsers);
        }
        
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
        return toResponse(task);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(String keyword, Pageable pageable) {
        return taskRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByFilters(
        String status, String priority, Long creatorId, Pageable pageable
    ) {
        Task.Status statusEnum = status != null ? Task.Status.valueOf(status.toUpperCase()) : null;
        Task.Priority priorityEnum = priority != null ? Task.Priority.valueOf(priority.toUpperCase()) : null;
        
        return taskRepository.findByFilters(statusEnum, priorityEnum, creatorId, pageable)
            .map(this::toResponse);
    }
    
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByAssignedUser(Long userId, Pageable pageable) {
        return taskRepository.findByAssignedUserId(userId, pageable).map(this::toResponse);
    }
    
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(parseStatus(request.getStatus()));
        task.setPriority(parsePriority(request.getPriority()));
        task.setDueDate(request.getDueDate());
        
        if (request.getAssignedUserIds() != null) {
            Set<User> assignedUsers = new HashSet<>(userRepository.findAllById(request.getAssignedUserIds()));
            task.setAssignedUsers(assignedUsers);
        }
        
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Tâche introuvable");
        }
        taskRepository.deleteById(id);
    }
    
    @Transactional
    public TaskResponse assignUserToTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        
        task.getAssignedUsers().add(user);
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    @Transactional
    public TaskResponse unassignUserFromTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Tâche introuvable"));
        
        task.getAssignedUsers().removeIf(user -> user.getId().equals(userId));
        task = taskRepository.save(task);
        return toResponse(task);
    }
    
    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .status(task.getStatus().name())
            .priority(task.getPriority().name())
            .dueDate(task.getDueDate())
            .creator(toUserResponse(task.getCreator()))
            .assignedUsers(task.getAssignedUsers().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toSet()))
            .createdAt(task.getCreatedAt())
            .updatedAt(task.getUpdatedAt())
            .build();
    }
    
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole().name())
            .enabled(user.getEnabled())
            .createdAt(user.getCreatedAt())
            .build();
    }
    
    private Task.Status parseStatus(String status) {
        if (status == null) return Task.Status.TODO;
        return Task.Status.valueOf(status.toUpperCase());
    }
    
    private Task.Priority parsePriority(String priority) {
        if (priority == null) return Task.Priority.MEDIUM;
        return Task.Priority.valueOf(priority.toUpperCase());
    }
}
