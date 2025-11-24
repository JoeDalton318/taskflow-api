package com.taskflow.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.api.dto.TaskRequest;
import com.taskflow.api.dto.TaskResponse;
import com.taskflow.api.dto.UserResponse;
import com.taskflow.api.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TaskService taskService;
    
    private TaskResponse taskResponse;
    private TaskRequest taskRequest;
    
    @BeforeEach
    void setUp() {
        UserResponse userResponse = UserResponse.builder()
            .id(1L)
            .email("test@example.com")
            .username("testuser")
            .role("USER")
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .build();
        
        taskResponse = TaskResponse.builder()
            .id(1L)
            .title("Test Task")
            .description("Test Description")
            .status("TODO")
            .priority("MEDIUM")
            .creator(userResponse)
            .assignedUsers(new HashSet<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        taskRequest = TaskRequest.builder()
            .title("Test Task")
            .description("Test Description")
            .status("TODO")
            .priority("MEDIUM")
            .build();
    }
    
    @Test
    @WithMockUser
    void createTask_ShouldReturnCreatedTask() throws Exception {
        when(taskService.createTask(any(TaskRequest.class), anyString())).thenReturn(taskResponse);
        
        mockMvc.perform(post("/api/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"));
    }
    
    @Test
    @WithMockUser
    void getTaskById_ShouldReturnTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);
        
        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"));
    }
    
    @Test
    @WithMockUser
    void getAllTasks_ShouldReturnPageOfTasks() throws Exception {
        Page<TaskResponse> taskPage = new PageImpl<>(Collections.singletonList(taskResponse));
        when(taskService.getAllTasks(any())).thenReturn(taskPage);
        
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("Test Task"));
    }
    
    @Test
    @WithMockUser
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(taskResponse);
        
        mockMvc.perform(put("/api/tasks/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
    
    @Test
    @WithMockUser
    void deleteTask_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/tasks/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
