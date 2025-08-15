package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Short taskId;
    private LocalDateTime taskCreateDate;
    private LocalDateTime taskUpdateDate;
    private String taskName;
    private String taskCreateBy;
    private String taskUpdateBy;
}