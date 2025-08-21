package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    private long expiredAt;
    
    public JwtResponse(String token, String username, String email, String role, long expiredAt) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiredAt = expiredAt;
    }
}