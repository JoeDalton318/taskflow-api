package com.taskflow.api.repository;

import com.taskflow.api.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    Page<Task> findByCreatorId(Long creatorId, Pageable pageable);
    
    @Query("SELECT t FROM Task t JOIN t.assignedUsers u WHERE u.id = :userId")
    Page<Task> findByAssignedUserId(@Param("userId") Long userId, Pageable pageable);
    
    Page<Task> findByStatus(Task.Status status, Pageable pageable);
    
    Page<Task> findByPriority(Task.Priority priority, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Task> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:creatorId IS NULL OR t.creator.id = :creatorId)")
    Page<Task> findByFilters(
        @Param("status") Task.Status status,
        @Param("priority") Task.Priority priority,
        @Param("creatorId") Long creatorId,
        Pageable pageable
    );
}
