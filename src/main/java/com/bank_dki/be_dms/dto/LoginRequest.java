package com.bank_dki.be_dms.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userEmail;
    private String userHashPassword;
}