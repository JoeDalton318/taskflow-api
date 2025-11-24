package com.taskflow.api.service;

import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.User;
import com.taskflow.api.repository.TaskRepository;
import com.taskflow.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private TaskService taskService;
    
    private User testUser;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .password("password")
            .role(User.Role.USER)
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        testTask = Task.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .status(Task.Status.TODO)
            .priority(Task.Priority.MEDIUM)
            .creator(testUser)
            .assignedUsers(new HashSet<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    void createTask_ShouldReturnTaskResponse() {
        // Given
        TaskRequest request = TaskRequest.builder()
            .title("New Task")
            .description("New Description")
            .status("TODO")
            .priority("HIGH")
            .build();
        
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse response = taskService.createTask(request, testUser.getEmail());
        
        // Then
        assertNotNull(response);
        assertEquals(testTask.getId(), response.getId());
        assertEquals(testTask.getTitle(), response.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    
    @Test
    void getTaskById_ShouldReturnTaskResponse() {
        // Given
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        // When
        TaskResponse response = taskService.getTaskById(1L);
        
        // Then
        assertNotNull(response);
        assertEquals(testTask.getId(), response.getId());
        assertEquals(testTask.getTitle(), response.getTitle());
    }
    
    @Test
    void getTaskById_ShouldThrowException_WhenTaskNotFound() {
        // Given
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999L));
    }
    
    @Test
    void getAllTasks_ShouldReturnPageOfTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(testTask));
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        
        // When
        Page<TaskResponse> response = taskService.getAllTasks(pageable);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(testTask.getTitle(), response.getContent().get(0).getTitle());
    }
    
    @Test
    void updateTask_ShouldReturnUpdatedTask() {
        // Given
        TaskRequest request = TaskRequest.builder()
            .title("Updated Task")
            .description("Updated Description")
            .status("IN_PROGRESS")
            .priority("HIGH")
            .build();
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse response = taskService.updateTask(1L, request);
        
        // Then
        assertNotNull(response);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    
    @Test
    void deleteTask_ShouldDeleteTask() {
        // Given
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);
        
        // When
        taskService.deleteTask(1L);
        
        // Then
        verify(taskRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void deleteTask_ShouldThrowException_WhenTaskNotFound() {
        // Given
        when(taskRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(999L));
    }
    
    @Test
    void assignUserToTask_ShouldAssignUser() {
        // Given
        User assignedUser = User.builder()
            .id(2L)
            .email("user2@example.com")
            .username("user2")
            .build();
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignedUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        
        // When
        TaskResponse response = taskService.assignUserToTask(1L, 2L);
        
        // Then
        assertNotNull(response);
        verify(taskRepository, times(1)).save(testTask);
    }
    
    @Test
    void searchTasks_ShouldReturnMatchingTasks() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Collections.singletonList(testTask));
        when(taskRepository.searchByKeyword("Test", pageable)).thenReturn(taskPage);
        
        // When
        Page<TaskResponse> response = taskService.searchTasks("Test", pageable);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }
}
