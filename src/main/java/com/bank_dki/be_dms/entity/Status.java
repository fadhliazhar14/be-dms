package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatusId")
    private Short statusId;
    
    @Column(name = "StatusOrderNumber", length = 40)
    private String statusOrderNumber;
    
    @Column(name = "StatusCreateDate")
    private LocalDateTime statusCreateDate;
    
    @Column(name = "StatusUpdateDate")
    private LocalDateTime statusUpdateDate;
    
    @Column(name = "StatusName", length = 50)
    private String statusName;
    
    @Column(name = "StatusCreateBy", length = 50)
    private String statusCreateBy;
    
    @Column(name = "StatusUpdateBy", length = 50)
    private String statusUpdateBy;
    
    @PrePersist
    protected void onCreate() {
        statusCreateDate = LocalDateTime.now();
        statusUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        statusUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(statusId, status.statusId) && 
               Objects.equals(statusName, status.statusName);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(statusId, statusName);
    }
}