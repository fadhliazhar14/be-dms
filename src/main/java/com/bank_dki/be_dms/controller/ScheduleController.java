package com.bank_dki.be_dms.controller;

import com.bank_dki.be_dms.dto.AddOperatorsToScheduleRequest;
import com.bank_dki.be_dms.dto.DeleteOperatorFromScheduleRequest;
import com.bank_dki.be_dms.dto.MessageResponse;
import com.bank_dki.be_dms.dto.OperatorTaskGroupDto;
import com.bank_dki.be_dms.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    @GetMapping("/operators-by-date")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<OperatorTaskGroupDto>> getOperatorsByDateGroupByTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        // Default to today if date is not provided
        if (date == null) {
            date = LocalDate.now();
        }
        
        List<OperatorTaskGroupDto> result = scheduleService.getOperatorsByDateGroupByTasks(date);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/add-operators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addOperatorsToSchedule(
            @RequestBody AddOperatorsToScheduleRequest request) {
        
        MessageResponse response = scheduleService.addOperatorsToSchedule(request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/remove-operator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteOperatorFromSchedule(
            @RequestBody DeleteOperatorFromScheduleRequest request) {
        
        MessageResponse response = scheduleService.deleteOperatorFromSchedule(request);
        return ResponseEntity.ok(response);
    }
}