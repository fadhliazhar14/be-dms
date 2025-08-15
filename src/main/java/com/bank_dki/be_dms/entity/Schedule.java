package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScheduleId")
    private Short scheduleId;
    
    @Column(name = "UserId", nullable = false)
    private Short userId;
    
    @Column(name = "TaskId", nullable = false)
    private Short taskId;
    
    @Column(name = "ScheduleCreateDate")
    private LocalDateTime scheduleCreateDate;
    
    @Column(name = "ScheduleUpdateDate")
    private LocalDateTime scheduleUpdateDate;
    
    @Column(name = "ScheduleCreateBy", length = 50)
    private String scheduleCreateBy;
    
    @Column(name = "ScheduleUpdateBy", length = 50)
    private String scheduleUpdateBy;
    
    @Column(name = "ScheduleTaskDate")
    private LocalDateTime scheduleTaskDate;
    
    @Column(name = "ScheduleDate")
    private LocalDate scheduleDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskId", insertable = false, updatable = false)
    private Task task;
    
    @PrePersist
    protected void onCreate() {
        scheduleCreateDate = LocalDateTime.now();
        scheduleUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        scheduleUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(scheduleId, schedule.scheduleId) && 
               Objects.equals(userId, schedule.userId) && 
               Objects.equals(taskId, schedule.taskId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(scheduleId, userId, taskId);
    }
}