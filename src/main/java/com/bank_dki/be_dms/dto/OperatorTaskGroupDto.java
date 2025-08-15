package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorTaskGroupDto {
    private Short taskId;
    private String taskName;
    private List<OperatorDto> operators;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatorDto {
        private Short userId;
        private String userName;
        private String userEmail;
        private String userJabatan;
    }
}