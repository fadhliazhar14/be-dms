package com.bank_dki.be_dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private long accessTokenExpiry;
    private String refreshToken;
    private long refreshTokenExpiry;
    private String type;
    private String username;
    private String email;
    private String role;

    @Data
    @AllArgsConstructor
    public static class JwtResponseForRefresh {
        private String accessToken;
        private long accessTokenExpiry;
    }
}