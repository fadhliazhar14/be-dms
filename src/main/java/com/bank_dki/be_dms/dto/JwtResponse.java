package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accesstoken;
    private long acessTokenExpiry;
    private String refreshToken;
    private long refrshtTokenExpiry;
    private String type;
    private String username;
    private String email;
    private String role;

    @Data
    @AllArgsConstructor
    public static class JwtResponseForRefresh {
        private String accesstoken;
        private long acessTokenExpiry;
    }
}