package com.taskflow.api.repository;

import com.taskflow.api.entity.Task;
import com.taskflow.api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private TaskRepository taskRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .email("test@example.com")
            .username("testuser")
            .password("password")
            .role(User.Role.USER)
            .enabled(true)
            .build();
        entityManager.persist(testUser);
        entityManager.flush();
    }
    
    @Test
    void findByCreatorId_ShouldReturnTasks() {
        // Given
        Task task = Task.builder()
            .title("Test Task")
            .description("Test Description")
            .status(Task.Status.TODO)
            .priority(Task.Priority.MEDIUM)
            .creator(testUser)
            .build();
        entityManager.persist(task);
        entityManager.flush();
        
        // When
        Page<Task> tasks = taskRepository.findByCreatorId(testUser.getId(), PageRequest.of(0, 10));
        
        // Then
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }
    
    @Test
    void findByStatus_ShouldReturnTasksWithStatus() {
        // Given
        Task task = Task.builder()
            .title("Test Task")
            .status(Task.Status.TODO)
            .priority(Task.Priority.MEDIUM)
            .creator(testUser)
            .build();
        entityManager.persist(task);
        entityManager.flush();
        
        // When
        Page<Task> tasks = taskRepository.findByStatus(Task.Status.TODO, PageRequest.of(0, 10));
        
        // Then
        assertTrue(tasks.getTotalElements() > 0);
    }
    
    @Test
    void searchByKeyword_ShouldReturnMatchingTasks() {
        // Given
        Task task = Task.builder()
            .title("Important Meeting")
            .description("Discuss project timeline")
            .status(Task.Status.TODO)
            .priority(Task.Priority.HIGH)
            .creator(testUser)
            .build();
        entityManager.persist(task);
        entityManager.flush();
        
        // When
        Page<Task> tasks = taskRepository.searchByKeyword("Meeting", PageRequest.of(0, 10));
        
        // Then
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Important Meeting", tasks.getContent().get(0).getTitle());
    }
}
