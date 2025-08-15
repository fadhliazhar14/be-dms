package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskId")
    private Short taskId;
    
    @Column(name = "TaskCreateDate")
    private LocalDateTime taskCreateDate;
    
    @Column(name = "TaskUpdateDate")
    private LocalDateTime taskUpdateDate;
    
    @Column(name = "TaskName", length = 50)
    private String taskName;
    
    @Column(name = "TaskCreateBy", length = 50)
    private String taskCreateBy;
    
    @Column(name = "TaskUpdateBy", length = 50)
    private String taskUpdateBy;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Log> logs;
    
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Schedule> schedules;
    
    @PrePersist
    protected void onCreate() {
        taskCreateDate = LocalDateTime.now();
        taskUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        taskUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(taskId, task.taskId) && 
               Objects.equals(taskName, task.taskName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(taskId, taskName);
    }
}