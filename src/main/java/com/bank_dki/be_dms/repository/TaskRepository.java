package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Short> {
    Optional<Task> findByTaskName(String taskName);
    
    @Query("SELECT t FROM Task t WHERE t.taskName LIKE %:name%")
    List<Task> findByTaskNameContaining(@Param("name") String name);
    
    Boolean existsByTaskName(String taskName);
}