package com.bank_dki.be_dms.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String userEmail;
    
    @Positive(message = "Role ID must be positive")
    private Short roleId;
    
    @Size(max = 100, message = "Birth date must not exceed 100 characters")
    private String userTglLahir;
    
    @Size(max = 40, message = "Position must not exceed 40 characters")
    private String userJabatan;
    
    @Size(max = 100, message = "Birth place must not exceed 100 characters")
    private String userTempatLahir;
    
    private Boolean userIsActive;
}
