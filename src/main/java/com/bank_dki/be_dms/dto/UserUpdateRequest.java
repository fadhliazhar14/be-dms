package com.bank_dki.be_dms.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String userName;
    private String userEmail;
    private Short roleId;
    private String userTglLahir;
    private String userJabatan;
    private String userTempatLahir;
    private Boolean userIsActive;
}