package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Short scheduleId;
    private Short userId;
    private Short taskId;
    private LocalDateTime scheduleCreateDate;
    private LocalDateTime scheduleUpdateDate;
    private String scheduleCreateBy;
    private String scheduleUpdateBy;
    private LocalDateTime scheduleTaskDate;
    private LocalDate scheduleDate;
    private String userName;
    private String taskName;
}