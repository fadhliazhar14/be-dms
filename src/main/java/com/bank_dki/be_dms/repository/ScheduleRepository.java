package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Short> {
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId")
    List<Schedule> findByUserId(@Param("userId") Short userId);
    
    @Query("SELECT s FROM Schedule s WHERE s.taskId = :taskId")
    List<Schedule> findByTaskId(@Param("taskId") Short taskId);
    
    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate = :date")
    List<Schedule> findByScheduleDate(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.task WHERE s.scheduleDate BETWEEN :startDate AND :endDate")
    List<Schedule> findSchedulesBetweenDatesWithDetails(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.user LEFT JOIN FETCH s.task WHERE s.scheduleDate = :date")
    List<Schedule> findByScheduleDateWithDetails(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND s.taskId = :taskId AND s.scheduleDate = :date")
    List<Schedule> findByUserIdAndTaskIdAndScheduleDate(@Param("userId") Short userId, @Param("taskId") Short taskId, @Param("date") LocalDate date);
    
    void deleteByUserIdAndTaskIdAndScheduleDate(Short userId, Short taskId, LocalDate scheduleDate);
}