package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Short> {
    Optional<Session> findBySessionToken(String sessionToken);
    
    @Query("SELECT s FROM Session s WHERE s.userId = :userId AND s.sessionIsRevoke = false")
    List<Session> findActiveSessionsByUserId(@Param("userId") Short userId);
    
    @Query("SELECT s FROM Session s WHERE s.sessionExpired = false AND s.sessionIsRevoke = false")
    List<Session> findAllActiveSessions();
    
    @Query("SELECT s FROM Session s WHERE s.sessionIpAddress = :ipAddress")
    List<Session> findBySessionIpAddress(@Param("ipAddress") String ipAddress);
}