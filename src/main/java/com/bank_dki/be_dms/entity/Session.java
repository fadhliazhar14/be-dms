package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SessionId")
    private Short sessionId;
    
    @Column(name = "SessionExpired")
    private Boolean sessionExpired;
    
    @Column(name = "SessionToken", length = 100)
    private String sessionToken;
    
    @Column(name = "SessionIpAddress", length = 512)
    private String sessionIpAddress;
    
    @Column(name = "SessionIsRevoke")
    private Boolean sessionIsRevoke;
    
    @Column(name = "SessionCreateDate")
    private LocalDateTime sessionCreateDate;
    
    @Column(name = "SessionUpdateDate")
    private LocalDateTime sessionUpdateDate;
    
    @Column(name = "UserId", nullable = false)
    private Short userId;
    
    @Column(name = "SessionCreateBy", length = 50)
    private String sessionCreateBy;
    
    @Column(name = "SessionUpdateBy", length = 50)
    private String sessionUpdateBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        sessionCreateDate = LocalDateTime.now();
        sessionUpdateDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        sessionUpdateDate = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(sessionId, session.sessionId) && 
               Objects.equals(sessionToken, session.sessionToken);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, sessionToken);
    }
}