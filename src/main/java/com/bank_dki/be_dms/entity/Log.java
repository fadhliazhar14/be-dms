package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogId")
    private Short logId;
    
    @Column(name = "LogName", length = 50)
    private String logName;
    
    @Column(name = "LogCreateDate")
    private LocalDate logCreateDate;
    
    @Column(name = "LogUpdateDate")
    private LocalDate logUpdateDate;
    
    @Column(name = "LogCreateBy", length = 50)
    private String logCreateBy;
    
    @Column(name = "LogUpdateBy", length = 50)
    private String logUpdateBy;
    
    @Column(name = "UserId")
    private Short userId;
    
    @Column(name = "TaskId")
    private Short taskId;
    
    @Column(name = "LogNote", length = 100)
    private String logNote;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskId", insertable = false, updatable = false)
    private Task task;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Log log = (Log) o;
        return Objects.equals(logId, log.logId) && 
               Objects.equals(logName, log.logName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(logId, logName);
    }
}