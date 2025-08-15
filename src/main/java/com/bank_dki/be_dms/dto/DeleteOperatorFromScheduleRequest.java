package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteOperatorFromScheduleRequest {
    private Short userId;
    private Short taskId;
    private LocalDate scheduleDate;
}