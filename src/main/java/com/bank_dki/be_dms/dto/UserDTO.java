package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Short userId;
    private String userName;
    private String userEmail;
    private LocalDateTime userCreateAt;
    private LocalDateTime userUpdateAt;
    private Boolean userIsActive;
    private Short roleId;
    private String userCreateBy;
    private String userUpdateBy;
    private String userTglLahir;
    private String userJabatan;
    private String userTempatLahir;
    private String roleName;
}