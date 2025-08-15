package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOperatorsToScheduleRequest {
    private List<Short> userIds;
    private Short taskId;
    private LocalDate scheduleDate;
    private String createdBy;
}