package com.bank_dki.be_dms.service;

import com.bank_dki.be_dms.dto.AddOperatorsToScheduleRequest;
import com.bank_dki.be_dms.dto.DeleteOperatorFromScheduleRequest;
import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.dto.OperatorTaskGroupDto;
import com.bank_dki.be_dms.entity.Schedule;
import com.bank_dki.be_dms.repository.ScheduleRepository;
import com.bank_dki.be_dms.repository.UserRepository;
import com.bank_dki.be_dms.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    
    public List<OperatorTaskGroupDto> getOperatorsByDateGroupByTasks(LocalDate date) {
        List<Schedule> schedules = scheduleRepository.findByScheduleDateWithDetails(date);
        
        Map<Short, List<Schedule>> groupedByTask = schedules.stream()
                .collect(Collectors.groupingBy(Schedule::getTaskId));
        
        return groupedByTask.entrySet().stream()
                .map(entry -> {
                    Short taskId = entry.getKey();
                    List<Schedule> taskSchedules = entry.getValue();
                    
                    String taskName = taskSchedules.isEmpty() ? "" : 
                            taskSchedules.get(0).getTask().getTaskName();
                    
                    List<OperatorTaskGroupDto.OperatorDto> operators = taskSchedules.stream()
                            .map(schedule -> new OperatorTaskGroupDto.OperatorDto(
                                    schedule.getUser().getUserId(),
                                    schedule.getUser().getUserName(),
                                    schedule.getUser().getUserEmail(),
                                    schedule.getUser().getUserJabatan()
                            ))
                            .collect(Collectors.toList());
                    
                    return new OperatorTaskGroupDto(taskId, taskName, operators);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MessageResponse addOperatorsToSchedule(AddOperatorsToScheduleRequest request) {
        // Validate task exists
        if (!taskRepository.existsById(request.getTaskId())) {
            return new MessageResponse("Error: Task with ID " + request.getTaskId() + " not found!");
        }
        
        // Validate all users exist
        for (Short userId : request.getUserIds()) {
            if (!userRepository.existsById(userId)) {
                return new MessageResponse("Error: User with ID " + userId + " not found!");
            }
        }
        
        // Create schedules for each operator
        for (Short userId : request.getUserIds()) {
            // Check if schedule already exists
            List<Schedule> existingSchedules = scheduleRepository.findByUserIdAndTaskIdAndScheduleDate(
                    userId, request.getTaskId(), request.getScheduleDate());
            
            if (existingSchedules.isEmpty()) {
                Schedule schedule = new Schedule();
                schedule.setUserId(userId);
                schedule.setTaskId(request.getTaskId());
                schedule.setScheduleDate(request.getScheduleDate());
                schedule.setScheduleCreateBy(request.getCreatedBy());
                schedule.setScheduleUpdateBy(request.getCreatedBy());
                
                scheduleRepository.save(schedule);
            }
        }
        
        return new MessageResponse("Operators added to schedule successfully!");
    }
    
    @Transactional
    public MessageResponse deleteOperatorFromSchedule(DeleteOperatorFromScheduleRequest request) {
        // Validate user exists
        if (!userRepository.existsById(request.getUserId())) {
            return new MessageResponse("Error: User with ID " + request.getUserId() + " not found!");
        }
        
        // Validate task exists
        if (!taskRepository.existsById(request.getTaskId())) {
            return new MessageResponse("Error: Task with ID " + request.getTaskId() + " not found!");
        }
        
        // Find and delete schedule
        List<Schedule> schedules = scheduleRepository.findByUserIdAndTaskIdAndScheduleDate(
                request.getUserId(), request.getTaskId(), request.getScheduleDate());
        
        if (schedules.isEmpty()) {
            return new MessageResponse("Error: Schedule not found for the specified operator, task, and date!");
        }
        
        scheduleRepository.deleteAll(schedules);
        
        return new MessageResponse("Operator removed from schedule successfully!");
    }
}