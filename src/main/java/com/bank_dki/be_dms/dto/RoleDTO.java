package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Short roleId;
    private LocalDateTime roleCreateDate;
    private LocalDateTime roleUpdateDate;
    private Boolean roleIsActive;
    private String roleName;
    private String roleCreateBy;
    private String roleUpdateBy;
}