package com.bank_dki.be_dms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String userEmail;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String userHashPassword;
    
    @Positive(message = "Role ID must be positive")
    private Short roleId;
    
    @Size(max = 100, message = "Birth date must not exceed 100 characters")
    private String userTglLahir;
    
    @Size(max = 40, message = "Position must not exceed 40 characters")
    private String userJabatan;
    
    @Size(max = 100, message = "Birth place must not exceed 100 characters")
    private String userTempatLahir;
}
