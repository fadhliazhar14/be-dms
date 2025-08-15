package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log, Short> {
    @Query("SELECT l FROM Log l WHERE l.userId = :userId ORDER BY l.logCreateDate DESC")
    List<Log> findByUserIdOrderByCreateDateDesc(@Param("userId") Short userId);
    
    @Query("SELECT l FROM Log l WHERE l.taskId = :taskId ORDER BY l.logCreateDate DESC")
    List<Log> findByTaskIdOrderByCreateDateDesc(@Param("taskId") Short taskId);
    
    @Query("SELECT l FROM Log l WHERE l.logCreateDate = :date")
    List<Log> findByLogCreateDate(@Param("date") LocalDate date);
    
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.user LEFT JOIN FETCH l.task ORDER BY l.logCreateDate DESC")
    List<Log> findAllWithDetails();
}